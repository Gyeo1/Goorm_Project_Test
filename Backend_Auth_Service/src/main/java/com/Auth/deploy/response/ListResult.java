package com.Auth.deploy.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResult<T> extends CommonResult {
    private List<T> list; //결과가 여러개일때 Respone용도로 사용한다. List에 결과를 담아서 응답(<T>로 여러 타입을 다 반송 가능!)
}