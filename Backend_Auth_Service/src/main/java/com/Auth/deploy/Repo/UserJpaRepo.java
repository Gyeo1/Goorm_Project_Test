package com.Auth.deploy.Repo;




import com.Auth.deploy.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<User, String> {

    Optional<User> findById(String user_id); //계정 구분짓기 위한 Uid를 위한 것, 이메일로 조회를 한다.


}