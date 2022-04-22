package com.Auth.deploy.response;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {//API 실행 결과를 담는 모델이다 상세 설명은 Value 참고

    @ApiModelProperty(value = "응답 성공여부 : true/false") //ApiModelProperty는 모델 내의 필드(변수)의 설명을 위해 사용
    private boolean success;

    @ApiModelProperty(value = "응답 코드 번호 : > 0 정상, < 0 비정상")
    private int code;

    @ApiModelProperty(value = "응답 메시지")
    private String msg;
}