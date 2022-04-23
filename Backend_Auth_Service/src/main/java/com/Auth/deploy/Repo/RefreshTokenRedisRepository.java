package com.Auth.deploy.Repo;

import com.Auth.deploy.Entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


//jpa 방식으로 Refrsh 토큰 저장.
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    Optional <RefreshToken> findById(String id);
}
