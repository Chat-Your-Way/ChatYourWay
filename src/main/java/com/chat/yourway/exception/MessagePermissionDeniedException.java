package com.chat.yourway.exception;

import com.chat.yourway.exception.handler.BaseRuntimeException;

public class MessagePermissionDeniedException extends BaseRuntimeException {
    public MessagePermissionDeniedException(String message) {
        super(message);
    }
}
