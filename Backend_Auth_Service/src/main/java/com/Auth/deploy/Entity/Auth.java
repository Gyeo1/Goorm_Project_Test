package com.Auth.deploy.Entity;


//Auth 정보 즉 Token을 위한 Entity

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@ToString
@Builder // builder를 사용할수 있게 합니다.
@Entity  // jpa entity임을 알립니다.
@Getter  // user 필드값의 getter를 자동으로 생성합니다.
@NoArgsConstructor // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "token") // 'token' 테이블과 매핑됨을 명시
@DynamicUpdate
@DynamicInsert
public class Auth {
    @Id
    private String user_id;

    @Column
    private String idx;
    @Column
    private String Access_token;

    @Column(unique = true)
    private String Refresh_token;

    @Column(updatable = false,insertable = true)
    private Timestamp timestamp;


}