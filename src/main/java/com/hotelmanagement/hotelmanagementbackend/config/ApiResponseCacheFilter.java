package com.hotelmanagement.hotelmanagementbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

/**
 * HTTP-level Redis caching filter for Spring Data REST endpoints.
 * 
 * These endpoints bypass the service layer (they go directly from
 * RepositoryRestResource → JPA Repository → Database), so @Cacheable
 * annotations on service methods don't help.
 * 
 * This filter caches the full HTTP JSON response body in Redis,
 * keyed by the full request URL (including query parameters).
 * On cache hit, the response is served directly from Redis without
 * touching JPA, Hibernate, or the database at all.
 */
@Component
public class ApiResponseCacheFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiResponseCacheFilter.class);

    private static final String CACHE_PREFIX = "http-cache::";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    // Spring Data REST paths to cache (public GET endpoints hit by all users)
    private static final Set<String> CACHEABLE_PREFIXES = Set.of(
            "/api/hotels",
            "/api/rooms",
            "/api/amenities",
            "/api/room-types",
            "/api/reviews"
    );

    // Paths that when modified should invalidate related caches
    private static final Set<String> EVICT_ON_WRITE_PREFIXES = Set.of(
            "/api/hotels",
            "/api/rooms",
            "/api/amenities",
            "/api/room-types",
            "/api/reviews",
            "/api/room-management",
            "/api/hotel-management",
            "/api/review-management",
            "/api/reservation",
            "/api/payment",
            "/api/roomAmenity"
    );

    private final StringRedisTemplate redisTemplate;

    public ApiResponseCacheFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String path = request.getRequestURI();

        // For write operations (POST/PUT/PATCH/DELETE), evict related caches
        if (!method.equals("GET") && !method.equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            evictRelatedCaches(path);
            return;
        }

        // Only cache GET requests to Spring Data REST endpoints
        if (!method.equals("GET") || !isCacheablePath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String cacheKey = buildCacheKey(request);

        // Try to serve from Redis cache
        try {
            String cachedBody = redisTemplate.opsForValue().get(cacheKey);
            if (cachedBody != null) {
                response.setContentType("application/hal+json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(200);
                response.getWriter().write(cachedBody);
                response.getWriter().flush();
                return;
            }
        } catch (Exception e) {
            // Redis unavailable — fall through to normal processing
            log.debug("Redis cache read failed, proceeding without cache: {}", e.getMessage());
        }

        // Cache miss — execute normally and capture response
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrappedResponse);

        // Only cache successful (2xx) responses
        if (wrappedResponse.getStatus() >= 200 && wrappedResponse.getStatus() < 300) {
            byte[] body = wrappedResponse.getContentAsByteArray();
            if (body.length > 0) {
                try {
                    String responseBody = new String(body, StandardCharsets.UTF_8);
                    redisTemplate.opsForValue().set(cacheKey, responseBody, CACHE_TTL);
                } catch (Exception e) {
                    log.debug("Redis cache write failed: {}", e.getMessage());
                }
            }
        }

        wrappedResponse.copyBodyToResponse();
    }

    private boolean isCacheablePath(String path) {
        for (String prefix : CACHEABLE_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private String buildCacheKey(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        return CACHE_PREFIX + uri + (query != null ? "?" + query : "");
    }

    private void evictRelatedCaches(String path) {
        try {
            for (String prefix : EVICT_ON_WRITE_PREFIXES) {
                if (path.startsWith(prefix)) {
                    // Evict all HTTP-cached responses for related resources
                    evictByPattern(CACHE_PREFIX + "/api/hotels*");
                    evictByPattern(CACHE_PREFIX + "/api/rooms*");
                    evictByPattern(CACHE_PREFIX + "/api/amenities*");
                    evictByPattern(CACHE_PREFIX + "/api/room-types*");
                    evictByPattern(CACHE_PREFIX + "/api/reviews*");
                    break;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache eviction failed: {}", e.getMessage());
        }
    }

    private void evictByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Evicted {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.debug("Failed to evict keys for pattern {}: {}", pattern, e.getMessage());
        }
    }
}
