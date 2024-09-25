package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class TopicAccessException extends BaseRuntimeException {
    public TopicAccessException(String message) {
        super(message);
    }
}
