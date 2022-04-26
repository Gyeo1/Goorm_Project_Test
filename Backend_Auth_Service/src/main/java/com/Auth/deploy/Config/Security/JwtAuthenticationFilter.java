package com.Auth.deploy.Config.Security;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Jwt가 유요한 토큰인지 판단하는 필터이다. UserPasswordAuthentication 필터 앞에 설정할 내용이다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //Once Per Filter?
//public class JwtAuthenticationFilter extends GenericFilterBean {
    //GenericFilterBean란 Filter의 확장 버전으로 Spring의 설정 정보 사용 가능!

    private final JwtTokenProvider jwtTokenProvider;

    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 1. Request Header 에서 토큰을 꺼냄
        String jwt = jwtTokenProvider.resolveToken(request);
        log.info("jwt 해더?: "+ jwt);
        // 2. validateToken 으로 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }


}
//
//    //실제 필터링 로직이 doFilterINternal로 들어간다.
//    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        //여기서 뭘하냐=>필터에 구현할 로직을 작성? OncePerRequestFilter는 doFilterInternal 함수 사용
//        //HttpServletRequest-> 클라이언트로 부터 오는 요청 정보를 담는 객체이다.
//        //SecurityContextHolder ->스프링 시큐리티가 인증한 내용을 담기 위해 사용
//
//
//        //resolveToken은 request 즉 Token에서 헤더를 분리해 순수 토큰 값을 token에 넣어줌
//        String token = jwtTokenProvider.resolveToken(request);
//
//        if (token != null && jwtTokenProvider.validateToken(token)) { //토큰이 있고 유효기간이 만료되지 않았다면
//            Authentication auth = jwtTokenProvider.getAuthentication(token); //인증 정보를 가져온다.
//            log.info("Filter 확인: "+auth.toString());
//            SecurityContextHolder.getContext().setAuthentication(auth); //인증한 내용을 Context 홀더에 담아준다.
//        }
//        filterChain.doFilter(request, response); //요청, 응답에 대한 필터링 진행
//    }
//}