package com.zerobase.reservation.service;

import com.zerobase.reservation.entity.Reservation;
import com.zerobase.reservation.entity.Review;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    // 리뷰 작성
    public Review writeReview(User user, Long reservationId, String comment, int rating) {
        Review review = Review.builder()
                .user(user)
                .reservation(Reservation.builder().reservationId(reservationId).build())
                .comment(comment)
                .rating(rating)
                .build();
        return reviewRepository.save(review);
    }
}
