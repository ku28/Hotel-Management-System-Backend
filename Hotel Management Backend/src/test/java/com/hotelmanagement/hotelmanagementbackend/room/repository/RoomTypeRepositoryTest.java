package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    @DisplayName("shouldReturnRoomTypesByNameIgnoreCase")
    void shouldReturnRoomTypesByNameIgnoreCase() {

        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury Room")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType standard = RoomType.builder()
                .typeName("Standard")
                .description("Standard Room")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("3000"))
                .build();

        roomTypeRepository.save(deluxe);
        roomTypeRepository.save(standard);

        Page<RoomType> result =
                roomTypeRepository.findByTypeNameContainingIgnoreCase(
                        "del",
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent().size())
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("shouldCheckIfRoomTypeExists")
    void shouldCheckIfRoomTypeExists() {

        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury Room")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        roomTypeRepository.save(deluxe);

        boolean exists =
                roomTypeRepository.existsByTypeName("Deluxe");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("shouldReturnFalseWhenRoomTypeDoesNotExist")
    void shouldReturnFalseWhenRoomTypeDoesNotExist() {

        boolean exists =
                roomTypeRepository.existsByTypeName("Suite");

        assertThat(exists).isFalse();
    }
}