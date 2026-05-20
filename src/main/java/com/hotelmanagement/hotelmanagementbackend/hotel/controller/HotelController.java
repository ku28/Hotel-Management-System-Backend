package com.hotelmanagement.hotelmanagementbackend.hotel.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel-management")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Hotel Management", description = "Hotel & Amenity Management APIs")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @DeleteMapping("/hotels/{hotelId}")
    @Operation(summary = "Soft delete a hotel")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Integer hotelId) {
        hotelService.softDeleteHotel(hotelId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Hotel deleted successfully", null));
    }

    @DeleteMapping("/amenities/{amenityId}")
    @Operation(summary = "Soft delete an amenity")
    public ResponseEntity<ApiResponse<Void>> deleteAmenity(@PathVariable Integer amenityId) {
        hotelService.softDeleteAmenity(amenityId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Amenity deleted successfully", null));
    }
}
