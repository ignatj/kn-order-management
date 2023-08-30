package com.ignatj.knordermanagement.common.exception;

public class ApiException extends RuntimeException{

    public ApiException(String message) {
        super(message);
    }
}
