package com.hotelmanagement.hotelmanagementbackend;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestHotelManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(HotelManagementBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
