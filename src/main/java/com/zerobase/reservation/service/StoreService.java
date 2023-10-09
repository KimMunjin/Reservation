package com.zerobase.reservation.service;

import com.zerobase.reservation.dto.StoreDTO;
import com.zerobase.reservation.entity.Store;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {
    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }

    // 매장 등록
    public Store registerStore(StoreDTO storeDTO, User partner){
        Store store = Store.builder()
                .storeName(storeDTO.getStoreName())
                .location(storeDTO.getLocation())
                .description(storeDTO.getDescription())
                .partner(partner)
                .build();
        Store savedStore = storeRepository.save(store);
        return savedStore;
    }

    // 전체 매장 가져오기
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();

        List<StoreDTO> storeDTOs = stores.stream()
                .map(this::convertToStoreDTO)
                .collect(Collectors.toList());
        return storeDTOs;
    }

    // 매장 상호명 검색
    public List<StoreDTO> searchStores(String keyword) {
        List<Store> stores = storeRepository.findByStoreNameContainingIgnoreCase(keyword);

        List<StoreDTO> storeDTOs = stores.stream()
                .map(this::convertToStoreDTO)
                .collect(Collectors.toList());
        return storeDTOs;
    }

    // 매장 상세 정보 출력
    public StoreDTO getStoreDetails(Long storeId) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if(storeOptional.isPresent()){
            Store store = storeOptional.get();
            return convertToStoreDTO(store);
        } else {
            return null;
        }
    }

    private StoreDTO convertToStoreDTO(Store store) {
        return StoreDTO.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .location(store.getLocation())
                .description(store.getDescription())
                .build();
    }


}
