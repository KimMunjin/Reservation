package com.zerobase.reservation.service;

import com.zerobase.reservation.entity.Review;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    void writeReview() {
        User user = new User();
        user.setUserId(1L);
        Long reservationId = 2L;
        String comment = "testcomment";
        int rating = 4;

        Review testReview = new Review();
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.writeReview(user, reservationId, comment, rating);

        assertEquals(testReview, result);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}