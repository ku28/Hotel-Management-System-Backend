package com.hotelmanagement.hotelmanagementbackend.review.repository;

import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setup() {

        reviewRepository.deleteAll();
    }

    @Test
    void shouldSaveReviewSuccessfully() {

        Review review = Review.builder()
                .rating(5)
                .comment("Excellent Service")
                .reviewDate(LocalDate.now())
                .build();

        Review savedReview =
                reviewRepository.save(review);

        assertNotNull(savedReview);

        assertNotNull(savedReview.getReviewId());
    }

    @Test
    void shouldFindReviewById() {

        Review review = Review.builder()
                .rating(4)
                .comment("Very Good")
                .reviewDate(LocalDate.now())
                .build();

        Review savedReview =
                reviewRepository.save(review);

        Optional<Review> foundReview =
                reviewRepository.findById(
                        savedReview.getReviewId()
                );

        assertTrue(foundReview.isPresent());

        assertEquals(
                "Very Good",
                foundReview.get().getComment()
        );
    }

    @Test
    void shouldReturnReviewsByRating() {

        Review review = Review.builder()
                .rating(5)
                .comment("Luxury Stay")
                .reviewDate(LocalDate.now())
                .build();

        reviewRepository.save(review);

        Page<Review> reviews =
                reviewRepository.findByRating(
                        5,
                        PageRequest.of(0, 5)
                );

        assertFalse(reviews.isEmpty());

        assertEquals(
                5,
                reviews.getContent().get(0).getRating()
        );
    }

    @Test
    void shouldReturnMostRecentReview() {

        Review oldReview = Review.builder()
                .rating(3)
                .comment("Old Review")
                .reviewDate(LocalDate.of(2025,1,1))
                .build();

        Review latestReview = Review.builder()
                .rating(5)
                .comment("Latest Review")
                .reviewDate(LocalDate.now())
                .build();

        reviewRepository.save(oldReview);

        reviewRepository.save(latestReview);

        Optional<Review> recentReview =
                reviewRepository
                        .findFirstByOrderByReviewDateDesc();

        assertTrue(recentReview.isPresent());

        assertEquals(
                "Latest Review",
                recentReview.get().getComment()
        );
    }

    @Test
    void shouldDeleteReviewSuccessfully() {

        Review review = Review.builder()
                .rating(2)
                .comment("Bad Service")
                .reviewDate(LocalDate.now())
                .build();

        Review savedReview =
                reviewRepository.save(review);

        reviewRepository.deleteById(
                savedReview.getReviewId()
        );

        Optional<Review> deletedReview =
                reviewRepository.findById(
                        savedReview.getReviewId()
                );

        assertFalse(deletedReview.isPresent());
    }
}