package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class TokenNotFoundException extends BaseRuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
