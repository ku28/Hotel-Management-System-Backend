package com.hotelmanagement.hotelmanagementbackend.review.repository;

import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findByRating(Integer rating, Pageable pageable);


    Optional<Review> findFirstByOrderByReviewDateDesc();

//    To-Do Resarvation

}
