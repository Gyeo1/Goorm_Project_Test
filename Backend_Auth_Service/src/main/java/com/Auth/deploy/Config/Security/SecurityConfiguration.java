package com.Auth.deploy.Config.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//서버에 적용될 Spring Security의 보안을 설정하는 핵심 부분이다.
@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtEntryPoint jwtEntryPoint;
    private static final String[] AUTH_LIST={ //Security에서 허용해줄 url 설정
            "/v2/api-docs","/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/*",
            "/webjars/**",
            "/swagger/**",
            "/swagger-ui/index.html",
            "/*/signin",
            "/*/signin/**",
            "/*/signup",
            "/*/signup/**",
            "/social/**",
            "/h2-console",
            "/api/**",
            "/resources/**",
            "/js/**",
            "/auth/**"
    };

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){ //PasswordEncoder란  패스워드를 암호화 하는 방식이다. 평문 저장을 막기 위함

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override //여기가 Spring 시큐리티 설정의 핵심
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
                .csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
                .logout().disable()// jwt로 로그인 로그아웃 할꺼니깐 disable
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증할것이므로 세션필요없으므로 생성안함.
                .and()
                .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크, 구분을 and로 하는구나 확인
                .antMatchers(AUTH_LIST).permitAll()  // 가입 및 인증 주소는 누구나 접근가능
                .antMatchers(HttpMethod.GET, "/exception/**","/helloworld/**", "/actuator/health").permitAll() // 등록된 GET요청 리소스는 누구나 접근가능
                .anyRequest().hasRole("USER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtEntryPoint); // 예외 handliing 인증/인가에서 예외는 Access_Token의 기간 만료다.
        ;
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        // jwt token 필터를 id/password 인증 필터 전에 넣으라는 의미이다.

    }

    @Override // 스웨거는 사용해야됨.
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**","/swagger-ui/index.html","/swagger-ui","/h2-console/**");

    }
}