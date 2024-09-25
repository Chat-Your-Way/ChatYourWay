package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class EmailTokenNotFoundException extends BaseRuntimeException {
    public EmailTokenNotFoundException() {
        super("Current token does not exist");
    }
}
