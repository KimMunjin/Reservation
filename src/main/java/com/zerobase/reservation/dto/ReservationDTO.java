package com.zerobase.reservation.dto;

import com.zerobase.reservation.type.ReservationStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long reservationId;
    private Long userId; // 사용자 테이블과의 연결을 위한 외래 키
    private Long storeId; // 매장 테이블과의 연결을 위한 외래 키
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reservationTime; // 예약 시간
    private ReservationStatus status; // 예약 상태 (확인 대기 중, 확인됨, 기한 만료)
}
