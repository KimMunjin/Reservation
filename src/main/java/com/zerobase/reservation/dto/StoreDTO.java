package com.zerobase.reservation.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {
    private Long storeId;
    private String storeName;
    private String location;
    private String description;
    private Long partnerId; // 사용자 테이블과 연결을 위한 키
}
