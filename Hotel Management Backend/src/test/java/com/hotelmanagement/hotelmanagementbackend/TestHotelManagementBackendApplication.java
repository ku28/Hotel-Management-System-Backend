package com.hotelmanagement.hotelmanagementbackend;

import org.springframework.boot.SpringApplication;

public class TestHotelManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(HotelManagementBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
