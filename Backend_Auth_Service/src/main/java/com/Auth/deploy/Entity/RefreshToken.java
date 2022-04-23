package com.Auth.deploy.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Column;
import javax.persistence.Id;

//핵심이 RedisHash이다 이 어노테이션을 붙이는 순간 Redis로 JPA가 연결해줌.
//기존의 DB 저장 방식은 Entity를 활용했기 때문에 확실하게 차이점을 볼 수 있다.

@Getter
@RedisHash("refreshToken") //Redis의 set 자료구조를 통해 valude 객체가 저장됨
@AllArgsConstructor
@Builder
public class RefreshToken {


    @Id
    private String id;

    private String refreshToken;



    @TimeToLive //TTL즉 이 시간이 지나면 자동으로 삭제되게 함
    private Long expiration;

    public static RefreshToken createRefreshToken(String username, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .id(username)
                .refreshToken(refreshToken)
                .expiration(remainingMilliSeconds / 1000)
                .build();
    }
}
