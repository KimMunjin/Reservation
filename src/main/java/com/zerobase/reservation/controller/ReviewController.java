package com.zerobase.reservation.controller;

import com.zerobase.reservation.dto.ReviewDTO;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 리뷰 작성 기능
    @ApiOperation("리뷰 작성")
    @PostMapping("/write")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> writeReview(@RequestBody ReviewDTO reviewDTO,
                                              @AuthenticationPrincipal User user) {
        if (user != null) {
            Long reservationId = reviewDTO.getReservationId();
            String comment = reviewDTO.getComment();
            int rating = reviewDTO.getRating();
            reviewService.writeReview(user, reservationId, comment, rating);
            log.info(reservationId+"리뷰 작성");
            return new ResponseEntity<>("리뷰 작성 성공", HttpStatus.CREATED);
        } else {
            log.error("리뷰 작성 실패");
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
    }
}
