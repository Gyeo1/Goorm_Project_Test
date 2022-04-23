package com.Auth.deploy.Config.Security;

import com.Auth.deploy.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class CustomUserDetails implements UserDetails {
//    private String username;
//    private String password;
//    @Builder.Default
//    private List<String> roles = new ArrayList<>();
//
//    public static UserDetails of(User user) {
//        return CustomUserDetails.builder()
//                .user(user.getNick_name())
//                .password(user.getPassword())
//                .roles(user.getRoles())
//                .build();
//    }
//}
