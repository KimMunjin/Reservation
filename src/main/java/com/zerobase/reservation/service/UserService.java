package com.zerobase.reservation.service;

import com.zerobase.reservation.dto.LoginRequest;
import com.zerobase.reservation.dto.UserDTO;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.UserRepository;
import com.zerobase.reservation.type.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // 회원 가입
    public User signUp(UserDTO userDTO) {
        boolean exists = this.userRepository.existsByUsername(userDTO.getUsername());
        if(exists) {
            throw new RuntimeException("이미 사용 중인 아이디 입니다.");
        }
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        UserType userType = userDTO.getUserType();

        String encodePassword = passwordEncoder.encode(password);

        User user = User.builder()
                .username(username)
                .password(encodePassword)
                .userType(userType)
                .build();
        User savedUser = userRepository.save(user);

        return savedUser;
    }

    // 로그인
    public User authenticate(LoginRequest loginRequest){
        User user = this.userRepository.findByUsername(loginRequest.getUsername())
                        .orElseThrow(()->new RuntimeException("ID와 비밀번호를 확인해주세요"));
        if(!this.passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            throw new RuntimeException("ID와 비밀번호를 확인해주세요");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("찾을 수 없는 user 입니다." + username));
    }
}
