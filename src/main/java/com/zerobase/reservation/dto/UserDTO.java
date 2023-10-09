package com.zerobase.reservation.dto;

import com.zerobase.reservation.type.UserType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String password;
    private UserType userType; // 일반 유저, 파트너

}
