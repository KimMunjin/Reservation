package com.zerobase.reservation.service;

import com.zerobase.reservation.dto.StoreDTO;
import com.zerobase.reservation.entity.Store;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.StoreRepository;
import com.zerobase.reservation.type.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Test
    void registerStore() {
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setStoreName("teststore");
        storeDTO.setLocation("testlocation");
        storeDTO.setDescription("testdescription");

        User partner = new User();
        partner.setUserId(1L);
        partner.setUsername("testuser");
        partner.setUserType(UserType.PARTNER);

        Store savedStore = new Store();
        savedStore.setStoreId(1L);
        savedStore.setStoreName("teststore");
        savedStore.setLocation("testlocation");
        savedStore.setDescription("testdescription");
        savedStore.setPartner(partner);
        when(storeRepository.save(Mockito.any())).thenReturn(savedStore);

        Store result = storeService.registerStore(storeDTO, partner);

        assertNotNull(result);
        assertEquals(1L, result.getStoreId());
        assertEquals("teststore", result.getStoreName());
        assertEquals("testlocation", result.getLocation());
        assertEquals("testdescription", result.getDescription());
        assertEquals(partner, result.getPartner());
    }
}