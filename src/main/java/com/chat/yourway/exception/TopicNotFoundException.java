package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class TopicNotFoundException extends BaseRuntimeException {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
