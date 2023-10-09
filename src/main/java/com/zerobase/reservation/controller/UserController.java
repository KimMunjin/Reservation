package com.zerobase.reservation.controller;

import com.zerobase.reservation.dto.LoginRequest;
import com.zerobase.reservation.dto.UserDTO;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.security.TokenProvider;
import com.zerobase.reservation.service.UserService;
import com.zerobase.reservation.type.UserType;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @Autowired
    public UserController(UserService userService, TokenProvider tokenProvider){
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }
    
    // 일반 사용자 회원 가입
    @ApiOperation("일반 회원 회원 가입")
    @PostMapping("/signup/user")
    public ResponseEntity<String> signUpUser(@RequestBody UserDTO userDTO){
        userDTO.setUserType(UserType.USER);
        User registeredUser = userService.signUp(userDTO);

        if (registeredUser != null) {
            log.info(registeredUser+" USER 회원 가입");
            return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
        } else {
            log.error("USER 회원 가입 실패");
            return new ResponseEntity<>("회원 가입 실패", HttpStatus.BAD_REQUEST);
        }
    }
    
    //파트너 회원 가입
    @ApiOperation("파트너 회원 가입")
    @PostMapping("/signup/partner")
    public ResponseEntity<String> signUpPartner(@RequestBody UserDTO userDTO){
        userDTO.setUserType(UserType.PARTNER);
        User registeredUser = userService.signUp(userDTO);

        if (registeredUser != null) {
            log.info(registeredUser+" PARTNER 회원 가입");
            return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
        } else {
            log.error("PARTNER 회원 가입 실패");
            return new ResponseEntity<>("회원 가입 실패", HttpStatus.BAD_REQUEST);
        }
    }

    //jwt 토큰 로그인 기능
    @ApiOperation("로그인")
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        User user = this.userService.authenticate(loginRequest);
        log.info(user.getUsername()+"Login");
        String token = this.tokenProvider.generateToken(user.getUsername(), user.getUserType());
        return ResponseEntity.ok(token);
    }

}
