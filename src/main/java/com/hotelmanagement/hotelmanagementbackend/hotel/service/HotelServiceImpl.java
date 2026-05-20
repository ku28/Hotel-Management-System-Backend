package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelAmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    public HotelServiceImpl(HotelRepository hotelRepository, AmenityRepository amenityRepository) {
        this.hotelRepository = hotelRepository;
        this.amenityRepository = amenityRepository;
    }

    @Override
    @CacheEvict(value = {"hotels", "rooms"}, allEntries = true)
    public void addAmenityToHotel(HotelAmenityRequestDto dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("hotel", "hotelId", dto.getHotelId()));
        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new ResourceNotFoundException("amenity", "amenityId", dto.getAmenityId()));
        hotel.getAmenities().add(amenity);
        hotelRepository.save(hotel);
    }

    @Override
    @CacheEvict(value = {"hotels", "rooms", "dashboard"}, allEntries = true)
    public void softDeleteHotel(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("hotel", "hotelId", hotelId));
        hotel.setDeleted(true);
        hotelRepository.save(hotel);
    }

    @Override
    @CacheEvict(value = {"hotels", "rooms"}, allEntries = true)
    public void softDeleteAmenity(Integer amenityId) {
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("amenity", "amenityId", amenityId));
        amenity.setDeleted(true);
        amenityRepository.save(amenity);
    }
}
