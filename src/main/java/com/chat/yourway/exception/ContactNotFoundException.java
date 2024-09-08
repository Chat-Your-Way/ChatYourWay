package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class ContactNotFoundException extends BaseRuntimeException {
    public ContactNotFoundException(String message) {
        super(message);
    }
}
