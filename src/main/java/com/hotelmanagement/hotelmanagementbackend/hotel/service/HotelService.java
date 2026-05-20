package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelAmenityRequestDto;

public interface HotelService {

    void addAmenityToHotel(HotelAmenityRequestDto dto);

    void softDeleteHotel(Integer hotelId);

    void softDeleteAmenity(Integer amenityId);
}
