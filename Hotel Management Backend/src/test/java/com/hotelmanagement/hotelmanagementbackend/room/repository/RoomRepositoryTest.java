package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
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
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    @DisplayName("shouldReturnAvailableRooms")
    void shouldReturnAvailableRooms() {

        Room room1 = Room.builder()
                .roomNumber(101)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(102)
                .isAvailable(false)
                .build();

        Room room3 = Room.builder()
                .roomNumber(103)
                .isAvailable(true)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        Page<Room> result =
                roomRepository.findByIsAvailableTrue(
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent().size())
                .isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("shouldReturnRoomsByRoomType")
    void shouldReturnRoomsByRoomType() {

        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType savedRoomType =
                roomTypeRepository.save(deluxe);

        Room room1 = Room.builder()
                .roomNumber(201)
                .roomType(savedRoomType)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(202)
                .roomType(savedRoomType)
                .isAvailable(false)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);

        Page<Room> result =
                roomRepository.findByRoomType_RoomTypeId(
                        savedRoomType.getRoomTypeId(),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent().size())
                .isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("shouldReturnAvailableRoomsByRoomType")
    void shouldReturnAvailableRoomsByRoomType() {

        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType savedRoomType =
                roomTypeRepository.save(deluxe);

        Room room1 = Room.builder()
                .roomNumber(301)
                .roomType(savedRoomType)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(302)
                .roomType(savedRoomType)
                .isAvailable(false)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);

        Page<Room> result =
                roomRepository.findByRoomType_RoomTypeIdAndIsAvailableTrue(
                        savedRoomType.getRoomTypeId(),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent().size())
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("shouldCheckIfRoomExists")
    void shouldCheckIfRoomExists() {

        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType savedRoomType =
                roomTypeRepository.save(deluxe);

        Room room = Room.builder()
                .roomNumber(401)
                .roomType(savedRoomType)
                .isAvailable(true)
                .build();

        roomRepository.save(room);

        boolean exists =
                roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(
                        401,
                        savedRoomType.getRoomTypeId()
                );

        assertThat(exists).isTrue();
    }
}