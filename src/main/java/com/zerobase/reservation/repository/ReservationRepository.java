package com.zerobase.reservation.repository;

import com.zerobase.reservation.dto.ReservationDTO;
import com.zerobase.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 예약시간 보다 10분 이상 남은 '확정 전'이고 APPROVED 인 예약 리스트
    @Query("SELECT r from Reservation r where r.status = 'BEFORE_CONFIRM' AND r.reservationDate >= :tenMinutesBefore AND r.approved = 'APPROVED'")
    List<Reservation> findApprovedReservations(LocalDateTime tenMinutesBefore);

}
