package com.Auth.deploy.Repo;


import com.Auth.deploy.Entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//jwt Token과 관련된 내용이다.
public interface AuthRepo extends JpaRepository<Auth, String> {
    Optional<Auth> findById(String refresh_token);

}
