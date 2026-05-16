package com.hotelmanagement.hotelmanagementbackend.reservation.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.BadRequestException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.ReservationMapper;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationRequestDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RoomRepository roomRepository,
                                  ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDto getReservationById(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", reservationId));
        return reservationMapper.toResponseDto(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservationResponseDto> getAllReservations(Pageable pageable) {
        Page<Reservation> page = reservationRepository.findByReservationIdGreaterThan(0, pageable);
        List<ReservationResponseDto> dtos = page.getContent().stream()
                .map(reservationMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservationResponseDto> getReservationsByEmail(String email, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findByGuestEmailIgnoreCase(email, pageable);
        List<ReservationResponseDto> dtos = page.getContent().stream()
                .map(reservationMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate getRoomAvailableAfter(Integer roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room", "roomId", roomId);
        }
        Page<Reservation> page = reservationRepository.findByRoom_RoomId(
                roomId, org.springframework.data.domain.PageRequest.of(
                        0, 1, org.springframework.data.domain.Sort.by("checkOutDate").descending()));
        return page.hasContent() ? page.getContent().get(0).getCheckOutDate() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservationResponseDto> getReservationsByDateRange(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Reservation> page = reservationRepository
                .findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(startDate, endDate, pageable);
        List<ReservationResponseDto> dtos = page.getContent().stream()
                .map(reservationMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public ReservationResponseDto updateReservation(Integer reservationId, ReservationRequestDto dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", reservationId));

        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", dto.getRoomId()));
            reservation.setRoom(room);
        }
        reservationMapper.updateEntity(reservation, dto);
        Reservation updated = reservationRepository.save(reservation);
        return reservationMapper.toResponseDto(updated);
    }

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void deleteReservation(Integer reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException("Reservation", "reservationId", reservationId);
        }
        reservationRepository.deleteById(reservationId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalReservations() {
        return reservationRepository.count();
    }
}
