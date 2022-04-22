package com.Auth.deploy.Exception;

public class KakaoTokenException extends RuntimeException {

    private final static String MESSAGE = "카카오 토큰 오류입니다.";

    public KakaoTokenException() {
        super(MESSAGE);
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
