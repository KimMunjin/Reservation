package com.zerobase.reservation.service;

import com.zerobase.reservation.dto.ReservationDTO;
import com.zerobase.reservation.entity.Reservation;
import com.zerobase.reservation.entity.Store;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.StoreRepository;
import com.zerobase.reservation.type.BeforeReservation;
import com.zerobase.reservation.type.ReservationStatus;
import com.zerobase.reservation.type.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, StoreRepository storeRepository) {
        this.reservationRepository = reservationRepository;
        this.storeRepository = storeRepository;
    }

    // 매장 예약
    public boolean reserve(ReservationDTO reservationDTO, Long userId) {
        Store store = storeRepository.findById(reservationDTO.getStoreId()).orElse(null);
        if(store==null){
            return false;
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime reservationTime = reservationDTO.getReservationTime();
        LocalDateTime minReservationTime = currentDateTime.plusMinutes(15);
        if(reservationTime.isBefore(minReservationTime)){
            //예약 시간이 현재 시간으로부터 15분 이내면 예약 불가
            return false;
        }
        Reservation reservation = Reservation.builder()
                .store(store)
                .user(User.builder().userId(userId).build())
                .reservationDate(reservationTime)
                .status(ReservationStatus.BEFORE_CONFIRM)
                .approved(BeforeReservation.PENDING)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);
        if(savedReservation != null) {
            return true;
        } else {
            return false;
        }
    }

    // 방문 확인
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new RuntimeException("예약을 찾을 수 없습니다."));

        if(reservation.getApproved()==BeforeReservation.APPROVED){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reservationTime = reservation.getReservationDate();

            LocalDateTime tenMinutesBeforeReservation = reservationTime.minusMinutes(10);

            if(now.isBefore(tenMinutesBeforeReservation)){
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservationRepository.save(reservation);
            } else {
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);
                throw new RuntimeException("유효한 기한이 만료되었습니다.");
            }
        } else {
            throw new RuntimeException("승인된 예약만 처리할 수 있습니다.");
        }
    }

    // 60초마다 방문 확정 시간 업데이트
    @Scheduled(fixedRate = 60000) //60초마다 실행
    public void processPendingReservation(){
        LocalDateTime now = LocalDateTime.now();
        //'승인' 되었고, 상태가 '확정 전'이고 현재 시간보다 예약 시간이 10분 이내인(만료되지 않은) 모든 예약을 DB에서 가져오기
        List<Reservation> approvedReservations = reservationRepository.findApprovedReservations(now);

        for(Reservation reservation : approvedReservations) {
            LocalDateTime tenMinutesBeforeReservation = reservation.getReservationDate().minusMinutes(10);

            if(now.isAfter(tenMinutesBeforeReservation)){
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);
            }
        }
    }

    // 예약 승인
    public boolean approveReservation(Long reservationId, User user) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation != null && isAuthorizedToApprove(user, reservation)&&reservation.getApproved()==BeforeReservation.PENDING) {
            reservation.setApproved(BeforeReservation.APPROVED);
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }

    //예약 거절
    public boolean rejectReservation(Long reservationId, User user) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        if (reservation != null && isAuthorizedToApprove(user, reservation) && reservation.getApproved()==BeforeReservation.PENDING) {
            reservation.setApproved(BeforeReservation.REJECTED);
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }

    public boolean isAuthorizedToApprove(User user, Reservation reservation) {
        // 사용자가 PARTNER이면서 예약된 매장의 파트너와 동일한 사용자인 경우에만 승인/거절 권한 부여
        return user.getUserType() == UserType.PARTNER &&
                user.getUserId().equals(reservation.getStore().getPartner().getUserId());
    }

    // 키오스크 화면 출력 위해 리스트 불러오는 메서드
    public List<ReservationDTO> getApprovedReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> approvedReservations = reservationRepository.findApprovedReservations(now);
        return mapToReservationDTOList(approvedReservations);
    }
    private List<ReservationDTO> mapToReservationDTOList(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> {
                    ReservationDTO reservationDTO = new ReservationDTO();
                    reservationDTO.setReservationId(reservation.getReservationId());
                    reservationDTO.setReservationTime(reservation.getReservationDate());
                    return reservationDTO;
                })
                .collect(Collectors.toList());
    }
}
