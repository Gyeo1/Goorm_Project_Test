package com.Auth.deploy.Service;


import com.Auth.deploy.Exception.CUserNotFoundException;
import com.Auth.deploy.Repo.UserJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


//토큰에 세팅된 유저 회원 정보를 조회 하는 서비스
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserJpaRepo userJpaRepo;

    public UserDetails loadUserByUsername(String user_id) { //UserDetail에서 user_id으로 유저 정보를 업데이트 한다.
        return userJpaRepo.findById(String.valueOf(user_id)).orElseThrow(CUserNotFoundException::new); //회원 정보 조회 에러시 NotFound발생

    }
}