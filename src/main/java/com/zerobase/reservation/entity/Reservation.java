package com.zerobase.reservation.entity;

import com.zerobase.reservation.type.BeforeReservation;
import com.zerobase.reservation.type.ReservationStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private LocalDateTime reservationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; //방문 확인 위한 status. BEFORE_CONFIRM, CONFIRMED, EXPIRED

    @Enumerated(EnumType.STRING)
    private BeforeReservation approved; // 예약 승인, 거절을 위한 approved PENDING, APPROVED, REJECTED
}
