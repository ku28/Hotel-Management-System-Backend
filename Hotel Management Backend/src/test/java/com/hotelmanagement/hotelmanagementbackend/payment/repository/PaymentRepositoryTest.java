package com.hotelmanagement.hotelmanagementbackend.payment.repository;

import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void cleanDatabase() {
        paymentRepository.deleteAll();
    }

    @Test
    void testSaveAndFindById() {

        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(150.00))
                .paymentDate(LocalDate.now())
                .paymentStatus("COMPLETED")
                .paymentMethod("CREDIT_CARD")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        assertThat(savedPayment.getPaymentId()).isNotNull();

        Optional<Payment> foundPayment =
                paymentRepository.findById(savedPayment.getPaymentId());

        assertThat(foundPayment).isPresent();

        assertThat(foundPayment.get().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150.00));

        assertThat(foundPayment.get().getPaymentStatus())
                .isEqualTo("COMPLETED");
    }

    @Test
    void testFindByPaymentStatusIgnoreCase() {

        paymentRepository.saveAll(List.of(

                Payment.builder()
                        .amount(BigDecimal.valueOf(100))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("PAID")
                        .paymentMethod("CARD")
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(200))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("pending")
                        .paymentMethod("CASH")
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(300))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("PAID")
                        .paymentMethod("UPI")
                        .build()
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Payment> result =
                paymentRepository.findByPaymentStatusIgnoreCase("paid", pageable);

        assertThat(result.getContent()).hasSize(2);
    }
}