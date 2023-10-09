package com.zerobase.reservation.repository;

import com.zerobase.reservation.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    // keyword를 포함한 상호명 검색 리스트
    List<Store> findByStoreNameContainingIgnoreCase(String keyword);
}
