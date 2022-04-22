package com.Auth.deploy.Controller;


import com.Auth.deploy.Config.Security.JwtTokenProvider;
import com.Auth.deploy.Entity.Auth;
import com.Auth.deploy.Entity.User;
import com.Auth.deploy.Entity.UserVo;
import com.Auth.deploy.Exception.CEmailSigninFailedException;
import com.Auth.deploy.Repo.AuthRepo;
import com.Auth.deploy.Repo.UserJpaRepo;
import com.Auth.deploy.Service.ResponseService;
import com.Auth.deploy.response.CommonResult;
import com.Auth.deploy.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;


//회원 가입 & 로그인 서비스
@RestController
@Slf4j
@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RequestMapping(value = "/auth", produces = "application/json;charset=utf-8")
@CrossOrigin(origins = "*") //리액트와 연동하기 위한 CROS 설정
public class SignController {
    @Autowired
    private final UserJpaRepo userJpaRepo;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final ResponseService responseService;
    @Autowired
    private final AuthRepo authRepo;
    @Autowired //로그인 안되던 이유..
    private final PasswordEncoder passwordEncoder;

    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "/signin")
    public SingleResult<String> signin(@ApiParam(value = "회원 로그인 Token 발급", required = true) @RequestBody UserVo userVo) {
//    public ListResult<String> signin(@ApiParam(value = "회원 로그인 Token 발급", required = true) @RequestBody UserVo userVo) {
        User userCheck = userJpaRepo.findById(userVo.getId()).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(userVo.getPassword(),userCheck.getPassword() )) //저장된 password와 받아온 password 비교
            throw new CEmailSigninFailedException();

        //토큰 2개를 return하기 위한 List return
        ArrayList<String> jwt =new ArrayList<String>();
        String refresh_token = jwtTokenProvider.createRefreshToken(String.valueOf(userCheck.getUser_id()));
        String access_token= jwtTokenProvider.createToken(String.valueOf(userCheck.getUser_id()), userCheck.getRoles());

        jwt.add(refresh_token);
        jwt.add(access_token);
        //Auth 정보 저장을 위해 토큰 값과 userID를 가져와 builder해준다.

        authRepo.save(Auth.builder().Refresh_token(refresh_token)
                .idx(Base64.getEncoder().encodeToString((userVo.getId()+access_token).getBytes(StandardCharsets.UTF_8)))
                .Access_token(access_token)
                .user_id(userVo.getId())
                .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build());
//        authRepo.getById("Refresh_token");
        //jwt에 Access 토큰과 refresh 토큰을 넣고 Tostring으로 보내준다.
        return responseService.getSingleResult(jwt.toString());
//        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user_check.getUser_id()), user_check.getRoles()));
    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public CommonResult signup(@ApiParam(value = "Api 요청 내용", required = true) @RequestBody User user2) {
//    public CommonResult signin(@ApiParam(value = "회원ID : 이메일", required = true) @RequestParam String id,
//                               @ApiParam(value = "비밀번호", required = true) @RequestParam String password,
//                               @ApiParam(value = "이름", required = true) @RequestParam String name) {


        userJpaRepo.save(User.builder()
                .user_id(user2.getUser_id())
                .password(passwordEncoder.encode(user2.getPassword()))
                .nick_name(user2.getNick_name())
                .email(user2.getEmail())
//                .age(user2.getAge())
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        return responseService.getSuccessResult();
    }

}
