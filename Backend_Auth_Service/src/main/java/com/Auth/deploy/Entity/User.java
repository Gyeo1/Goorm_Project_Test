package com.Auth.deploy.Entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Builder // builder를 사용할수 있게 합니다.
@Entity  // jpa entity임을 알립니다.
@Getter  // user 필드값의 getter를 자동으로 생성합니다.
@NoArgsConstructor // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "member") // 'user' 테이블과 매핑됨을 명시
public class User implements UserDetails { //user Detail을 상속받는다 즉 Spring Security의 보안적용을 위한 추가 정보를 받는다.

    @Id // pk 유저 id는 유니크, uid로 pk값 지정한다.
    private String user_id;

//    @Column(nullable = false, unique = true, length = 30) //유니크 옵션으로 uid는 유일해야됨 uid는 회원 구분 id이다.

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = true, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String nick_name;


    @Column(nullable = true, length = 100)
    private String email;


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>(); //role은 회원이 가지고 있는 권한의 정보 가입시 기본으로 USER 역할이 부여됨

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.nick_name;

    }

    //아래는 Security에서 사용되는 값이다.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {//계정이 만료가 안됐는지
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() { //계정이 잠겼는지
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {//패스워드가 만료 됐는wl
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() { //계정 사용 가능한지
        return true;
    }
}