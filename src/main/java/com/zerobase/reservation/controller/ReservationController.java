package com.zerobase.reservation.controller;

import com.zerobase.reservation.dto.ReservationDTO;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.service.ReservationService;
import com.zerobase.reservation.type.UserType;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    // 매장 예약 기능
    @ApiOperation("매장 예약")
    @PostMapping("/reserve")
    @PreAuthorize("hasRole('USER')") // userType이 USER인 회원만 예약 가능
    public ResponseEntity<?> reserve(@RequestBody ReservationDTO reservationDTO,
                                     @AuthenticationPrincipal User user) {
        if(user != null && user.getUserType() == UserType.USER) {
            boolean reservationSuccess = reservationService.reserve(reservationDTO, user.getUserId());

            if(reservationSuccess){
                log.info("예약 완료 : "+reservationDTO.getStoreId()+"예약자 : "+ user.getUsername());
                return ResponseEntity.ok("예약이 성공적으로 완료되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("예약에 실패했습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("예약 가능한 사용자가 아닙니다.");
        }
    }

    // 매장 예약 승인 기능
    @ApiOperation("매장 예약 승인")
    @PostMapping("/approve/{reservationId}")
    @PreAuthorize("hasRole('PARTNER')") // userType이 PARTNER 인 회원만 접근 가능
    public ResponseEntity<String> approveReservation(@PathVariable Long reservationId, @AuthenticationPrincipal User user){
        boolean success = reservationService.approveReservation(reservationId, user);
        // 현재 로그인한 정보를 같이 넘겨서 매장의 파트너 id와 로그인한 userid가 동일해야 승인이 가능하도록 Service에서 구현
        if (success) {
            log.info(reservationId+" 예약 승인");
            return ResponseEntity.ok("예약이 승인되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("예약을 찾을 수 없거나 예약 승인 권한이 없습니다.");
        }
    }

    // 매장 예약 거절
    @ApiOperation("매장 예약 거절")
    @PostMapping("/reject/{reservationId}")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<String> rejectReservation(@PathVariable Long reservationId,
                                                    @AuthenticationPrincipal User user) {
        boolean success = reservationService.rejectReservation(reservationId, user);
        // 현재 로그인한 정보를 같이 넘겨서 매장의 파트너 id와 로그인한 userid가 동일해야 거절이 가능하도록 Service에서 구현
        if (success) {
            log.info(reservationId+" 예약 거절");
            return ResponseEntity.ok("예약이 거절되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("예약을 찾을 수 없거나 예약 거절 권한이 없습니다.");
        }
    }

    // 매장 방문 후 확인 기능
    @ApiOperation("매장 방문 후 방문 확인")
    @PostMapping("/confirm/{reservationId}")
    public ResponseEntity<String> confirmReservation(@PathVariable Long reservationId) {
        try{
            reservationService.confirmReservation(reservationId);
            log.info(reservationId+" 매장 방문 확인");
            return ResponseEntity.ok("방문이 확인되었습니다.");
        }catch (RuntimeException e) {
            log.error(reservationId+" 매장 방문 확인 실패");
            return ResponseEntity.badRequest().body("방문 확인 실패했습니다.");
        }
    }

    // 키오스크에 출력할 예약 명단. 승인된 예약 중 확정 전인 예약만 출력한다
    @ApiOperation("키오스크 화면에 출력할 예약 명단")
    @GetMapping("/kiosklist")
    public ResponseEntity<List<ReservationDTO>> getApprovedReservations() {
        List<ReservationDTO> approvedReservations = reservationService.getApprovedReservations();
        return ResponseEntity.ok(approvedReservations);
    }

}
