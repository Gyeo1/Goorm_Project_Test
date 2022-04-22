package com.Auth.deploy.Exception;

public class CEmailSigninFailedException extends RuntimeException { //이메일 로그인 실패시
    public CEmailSigninFailedException(String msg, Throwable t) {
        super(msg, t);
    }

    public CEmailSigninFailedException(String msg) {
        super(msg);
    }

    public CEmailSigninFailedException() {
        super();
    }
}