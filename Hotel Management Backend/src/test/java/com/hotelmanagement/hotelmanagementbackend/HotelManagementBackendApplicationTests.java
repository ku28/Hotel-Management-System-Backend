package com.hotelmanagement.hotelmanagementbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class HotelManagementBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
