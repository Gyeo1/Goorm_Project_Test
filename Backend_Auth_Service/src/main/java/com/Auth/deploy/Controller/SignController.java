package com.Auth.deploy.Controller;


import com.Auth.deploy.Config.Security.JwtTokenProvider;
import com.Auth.deploy.Entity.Auth;
import com.Auth.deploy.Entity.RefreshToken;
import com.Auth.deploy.Entity.User;
import com.Auth.deploy.Entity.UserVo;
import com.Auth.deploy.Exception.CEmailSigninFailedException;
import com.Auth.deploy.Repo.AuthRepo;
import com.Auth.deploy.Repo.RefreshTokenRedisRepository;
import com.Auth.deploy.Repo.UserJpaRepo;
import com.Auth.deploy.Service.ResponseService;
import com.Auth.deploy.response.CommonResult;
import com.Auth.deploy.response.SingleResult;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    //Refresh 토큰 관련 정보 저장
    @Autowired
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "/login")
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
        log.info(refresh_token);
        log.info(userVo.getId());
        //refresh 토큰을 Redis 저장소로 저장한다.
        refreshTokenRedisRepository.save(
                RefreshToken.createRefreshToken(
                        userVo.getId(),
                        refresh_token,
                        60 * 1000L) //1분임
        );
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

    //Access 토큰이 잘 오기만 하면 된다.
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token을 넣으세요", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "Token 유효 검사", notes = "Validaiton!")
    @GetMapping(value = "/validate")
    public CommonResult checkToken(String key){
        log.info(key);//

        return responseService.getSuccessResult(); //잘 들어오기만 하면 된다. 왜냐면 이미 들어오는거 자체가 Filter를 거치기 때문.
    }



    @ApiOperation(value = "Refresh Token 인증")
    @PostMapping(value = "/refresh")
    public CommonResult  refreshProcess(String key){
        String userid = jwtTokenProvider.informationToken(key).toString(); //refresh 토큰에 있는 유저 정보를 가져옴
        User userCheck = userJpaRepo.findById(userid).orElseThrow(CEmailSigninFailedException::new); //해당 토큰의 유저가 DB에 있는지
        log.info("유저 ? : "+ userid);


        if (refreshTokenRedisRepository.findById(userid).toString().equals("Optional.empty")) //empty면 Redis에 User관련 토큰이 x
        {
            log.info("Refresh 토큰이 만료되었습니다. Refrsh/Access를 재발급 합니다.");

            //토큰 2개 재생성
            ArrayList<String> jwt =new ArrayList<String>();
            String refresh_token = jwtTokenProvider.createRefreshToken(String.valueOf(userCheck.getUser_id()));
            String access_token= jwtTokenProvider.createToken(String.valueOf(userCheck.getUser_id()), userCheck.getRoles());

            jwt.add(refresh_token);
            jwt.add(access_token);

            //Redis에 Refresh token 저장.
            refreshTokenRedisRepository.save(
                    RefreshToken.createRefreshToken(
                            userid,
                            refresh_token,
                            60 * 3* 1000L) //1분임
            );
            return responseService.getSingleResult(jwt.toString());
        }
        else{
            String access_token= jwtTokenProvider.createToken(String.valueOf(userCheck.getUser_id()), userCheck.getRoles());
            log.info("Refresh 확인 Access_Token 재 발급");
            return responseService.getSingleResult(access_token);
        }


    }
    @ApiOperation(value = "로그아웃", notes = "회원을 삭제한다.")
    @PostMapping("/logout")
    public SingleResult<String> logout(@RequestBody String key) {
        String userid = jwtTokenProvider.informationToken(key).toString(); //유저 이름을 받아옴
        log.info("로그아웃 하려는 유저 ? : "+ userid);
        refreshTokenRedisRepository.deleteById(userid);  //유저 이름을 기준으로 Refresh 토큰 삭제

        return responseService.getSingleResult("LogOut이 완료되었습니다");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "회원번호(user_id)로 회원정보를 삭제한다")
    @DeleteMapping(value = "/user/{user_id}")
    public CommonResult delete(
            @ApiParam(value = "회원번호", required = true) @PathVariable String user_id) {
        userJpaRepo.deleteById(user_id);

        // 성공 결과 정보만 필요한경우 getSuccessResult()를 이용하여 결과를 출력한다.
        return responseService.getSuccessResult();
    }

}
