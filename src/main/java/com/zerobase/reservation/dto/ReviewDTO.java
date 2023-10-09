package com.zerobase.reservation.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long reservationId;
    private Long userId; // 사용자 테이블과의 연결을 위한 외래 키
    private Long storeId; // 매장 테이블과의 연결을 위한 외래 키
    private int rating; // 별점 또는 평가 점수
    private String comment; // 리뷰 코멘트
}
