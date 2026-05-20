package com.hotelmanagement.hotelmanagementbackend.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configure ObjectMapper with JavaTimeModule for LocalDate/LocalDateTime support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // Per-cache TTL configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Dashboard cache: shorter TTL (5 min) since it aggregates data
        cacheConfigurations.put("dashboard", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Reservations: moderate TTL (10 min) — changes with bookings
        cacheConfigurations.put("reservations", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Reviews: longer TTL (15 min) — changes less frequently
        cacheConfigurations.put("reviews", defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // Payments: longer TTL (15 min) — rarely changes after creation
        cacheConfigurations.put("payments", defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // Rooms: moderate TTL (10 min) — changes with availability
        cacheConfigurations.put("rooms", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Hotels: longer TTL (20 min) — master data, rarely changes
        cacheConfigurations.put("hotels", defaultConfig.entryTtl(Duration.ofMinutes(20)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
