package com.zerobase.reservation.controller;

import com.zerobase.reservation.dto.StoreDTO;
import com.zerobase.reservation.entity.Store;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.security.TokenProvider;
import com.zerobase.reservation.service.StoreService;
import com.zerobase.reservation.service.UserService;
import com.zerobase.reservation.type.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stores")
public class StoreController {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final StoreService storeService;

    @Autowired
    public StoreController(TokenProvider tokenProvider, UserService userService, StoreService storeService){
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.storeService = storeService;
    }

    // 매장 등록 기능
    @ApiOperation("매장 등록")
    @PostMapping("/register")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<String> registerStore(@RequestBody StoreDTO storeDTO, @AuthenticationPrincipal User user){
        if(user.getUserType()!= UserType.PARTNER){
            return ResponseEntity.badRequest().body("파트너 유저만 매장 등록을 할 수 있습니다.");
        }
        Store savedStore = storeService.registerStore(storeDTO, user);

        if(savedStore != null) {
            log.info("매장 등록 : "+savedStore.getStoreName()+"파트너 : "+user.getUsername());
            return ResponseEntity.ok("매장이 성공적으로 등록되었습니다.");
        } else {
            log.error("매장 등록 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("매장 등록에 실패했습니다.");
        }
    }

    // 매장 검색 전 띄울 전체 매장 리스트 출력
    @ApiOperation("전체 매장 리스트")
    @GetMapping("/all")
    public ResponseEntity<?> getAllStores(){
        log.info("전체 매장 목록 출력");
        List<StoreDTO> stores = storeService.getAllStores();
        if(!stores.isEmpty()){
            return ResponseEntity.ok(stores);
        } else {
            log.error("매장 목록 출력 에러");
            return ResponseEntity.notFound().build();
        }
    }

    // 상호명 검색 기능
    @ApiOperation("상호명을 통한 매장 검색")
    @GetMapping("/search")
    public ResponseEntity<?> searchStores(@RequestParam("keyword")String keyword){
        log.info("상호명 검색 : "+ keyword);
        List<StoreDTO> stores = storeService.searchStores(keyword);
        if(!stores.isEmpty()){
            return ResponseEntity.ok(stores);
        } else {
            log.error("상호명 검색 실패");
            return ResponseEntity.notFound().build();
        }
    }

    // 매장 선택 시 출력시킬 매장 상세 정보
    @ApiOperation("매장 상세 정보 보기")
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreDetails(@PathVariable Long storeId) {
        StoreDTO storeDetails = storeService.getStoreDetails(storeId);

        if(storeDetails!=null){
            return ResponseEntity.ok(storeDetails);
        } else {
            log.error("상세 정보 보기 실패");
            return ResponseEntity.notFound().build();
        }
    }
}
