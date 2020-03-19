package com.jf.exception;

/**
 * app异常
 * Created by admin on 2018/5/24.
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

}
