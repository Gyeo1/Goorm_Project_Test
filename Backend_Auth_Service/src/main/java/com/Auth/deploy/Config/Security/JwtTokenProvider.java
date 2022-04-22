package com.Auth.deploy.Config.Security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider { // JWT 토큰을 생성 및 검증 모듈

    @Value("spring.jwt.secret")
    private String secretKey;

    private long tokenValidMilisecond = 1000L * 2 * 60; // 1시간만 토큰 유효
    private long refreshTokenValidMillisecond = 60 * 60 * 24 * 1000L; //하루 유효 기간.
    private final UserDetailsService userDetailsService; // 유저 정보를 저장하기 위한 객체 생성

    @PostConstruct
    protected void init() { //init은 최초 실행시만 실행됨
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Jwt 토큰 생성
    public String createToken(String userPk, List<String> roles){  //
        Claims claims = Jwts.claims().setSubject(userPk); //유저 정보 저장용 claims
        claims.put("roles", roles);//역할을 넣는다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 유효 기간.
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    //Refresh 토큰 생성
    public String createRefreshToken(String userPk){
        Date now =new Date(); //만료 시간만 넣어준다
        Claims claims = Jwts.claims().setSubject(userPk);
        return Jwts.builder()
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token)); //loadUser~~이건 유저 정보를 가져옴 UserPK로 가져오는듯
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt 토큰에서 회원 구별 정보 추출
    public String getUserPk(String token) { //토큰을 받아서 파싱을한다는 의미.
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 파싱 : "X-AUTH-TOKEN: jwt토큰"
    public String resolveToken(HttpServletRequest req) {
        return req.getHeader("X-AUTH-TOKEN");
    }

    // Jwt 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);//파싱으로 만료일자 확인.

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) { //기간 만료시 여기로 온다 -> 재발급 토큰을 db에서 확인 후 create token을 return
            log.info("기간이 만료된 토큰입니다.");
            return false;
        }
    }
    public Object informationToken(String jwt){
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);

//        log.info("getsubject: "+claims.getBody().getSubject());
//        log.info("getAudience: "+claims.getBody().getAudience());
//        log.info("getId: "+claims.getBody().getId());
//        log.info("getExpiration: "+claims.getBody().getExpiration().before(new Date()));
        return claims.getBody().getSubject(); //subject가 user id를 담고 있음
    }
}