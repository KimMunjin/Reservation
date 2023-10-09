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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StoreRepository storeRepository;


    @Test
    void reserve() {
        ReservationDTO testReservationDTO = new ReservationDTO();
        testReservationDTO.setStoreId(1L);
        testReservationDTO.setReservationTime(LocalDateTime.now().plusMinutes(30));

        Store testStore = new Store();
        testStore.setStoreId(1L);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));

        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setReservationId(1L);
            return reservation;
        });

        boolean result = reservationService.reserve(testReservationDTO, 1L);

        assertTrue(result);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void confirmReservation() {
        Long reservationId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationTime = now.plusMinutes(15); // 5분 후 예약
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setReservationDate(reservationTime);
        reservation.setApproved(BeforeReservation.APPROVED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservationService.confirmReservation(reservationId);

        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);

    }

    @Test
    void approveReservation() {
        Long reservationId = 1L;
        User user = new User();
        user.setUserId(1L);
        user.setUserType(UserType.PARTNER);

        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("teststore");
        store.setPartner(user);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime minReservationTime = currentDateTime.plusMinutes(25);

        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setStore(store);
        reservation.setReservationDate(minReservationTime);
        reservation.setStatus(ReservationStatus.BEFORE_CONFIRM);
        reservation.setApproved(BeforeReservation.PENDING);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        boolean result = reservationService.approveReservation(reservationId, user);

        assertTrue(result);
        assertEquals(BeforeReservation.APPROVED, reservation.getApproved());

        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void getApprovedReservations() {
        LocalDateTime now = LocalDateTime.now();

        Reservation reservation1 = new Reservation();
        reservation1.setReservationId(1L);
        reservation1.setReservationDate(now.plusMinutes(30));
        reservation1.setStatus(ReservationStatus.BEFORE_CONFIRM);
        reservation1.setApproved(BeforeReservation.APPROVED);

        Reservation reservation2 = new Reservation();
        reservation2.setReservationId(2L);
        reservation2.setReservationDate(now.plusHours(1));
        reservation2.setStatus(ReservationStatus.BEFORE_CONFIRM);
        reservation2.setApproved(BeforeReservation.APPROVED);

        List<Reservation> approvedReservations = new ArrayList<>();
        approvedReservations.add(reservation1);
        approvedReservations.add(reservation2);

        when(reservationRepository.findApprovedReservations(Mockito.any(LocalDateTime.class))).thenReturn(approvedReservations);

        List<ReservationDTO> result = reservationService.getApprovedReservations();

        assertEquals(2, result.size());
    }

}