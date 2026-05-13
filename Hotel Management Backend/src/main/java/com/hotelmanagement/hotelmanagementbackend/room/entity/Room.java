package com.hotelmanagement.hotelmanagementbackend.room.entity;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_number")
    private Integer roomNumber;
    //add joins later x2

    @Column(name = "is_available")
    private Boolean isAvailable;

    //add join x1
    @Builder.Default
    private Set<Amenity> amenities = new HashSet<>();
}
