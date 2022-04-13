package com.binariks.techtask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class OAuthExc extends RuntimeException{
    public OAuthExc() {
        super();
    }

    public OAuthExc(String message) {
        super(message);
    }
}
