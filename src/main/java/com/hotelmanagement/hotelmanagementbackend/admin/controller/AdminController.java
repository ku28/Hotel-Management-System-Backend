package com.hotelmanagement.hotelmanagementbackend.admin.controller;

import com.hotelmanagement.hotelmanagementbackend.auth.dto.UserResponseDto;
import com.hotelmanagement.hotelmanagementbackend.auth.entity.User;
import com.hotelmanagement.hotelmanagementbackend.auth.repository.UserRepository;
import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.payment.service.PaymentService;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import com.hotelmanagement.hotelmanagementbackend.reservation.service.ReservationService;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin", description = "Admin Dashboard APIs")
public class AdminController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public AdminController(ReservationService reservationService,
                           PaymentService paymentService,
                           ReviewService reviewService,
                           UserRepository userRepository,
                           ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalReservations = reservationService.getTotalReservations();
        BigDecimal totalRevenue = paymentService.getTotalRevenue();
        long totalUsers = userRepository.count();

        PagedResponse<ReservationResponseDto> recentBookings = reservationService
                .getAllReservations(PageRequest.of(0, 10, Sort.by("reservationId").descending()));

        PagedResponse<ReviewResponseDto> recentReviews = reviewService
                .getAllReviews(PageRequest.of(0, 10));

        stats.put("totalReservations", totalReservations);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalUsers", totalUsers);
        stats.put("recentBookings", recentBookings.getContent());
        stats.put("recentReviews", recentReviews.getContent());

        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Dashboard stats retrieved", stats));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users with reservation counts")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Page<User> userPage = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("userId").descending()));

        List<UserResponseDto> dtos = userPage.getContent().stream()
                .map(user -> UserResponseDto.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .enabled(user.getEnabled())
                        .reservationCount(reservationRepository
                                .countByGuestEmailIgnoreCaseAndDeletedFalse(user.getEmail()))
                        .build())
                .toList();

        PagedResponse<UserResponseDto> response = PagedResponseMapper.toPagedResponse(userPage, dtos);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Users retrieved", response));
    }
}

