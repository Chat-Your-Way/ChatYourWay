package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class ContactEmailNotExist extends BaseRuntimeException {
    public ContactEmailNotExist(String message) {
        super(message);
    }
}
