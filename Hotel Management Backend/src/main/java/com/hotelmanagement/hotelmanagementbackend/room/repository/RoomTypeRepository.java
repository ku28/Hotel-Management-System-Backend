package com.hotelmanagement.hotelmanagementbackend.room.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    Page<RoomType> findByTypeNameContainingIgnoreCase(String typeName, Pageable pageable);

    boolean existsByTypeName(String typeName);
}
