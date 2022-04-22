package com.Auth.deploy.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResult<T> extends CommonResult { //결과가 단일건인 경우 호출되는 메소드다 <T>란 어떤 형태의 값도 넣을 수 있다는 의미
    //<>는 제네릭으로 데이터 타입의 일반화를 위해 사용함
    private T data;
}