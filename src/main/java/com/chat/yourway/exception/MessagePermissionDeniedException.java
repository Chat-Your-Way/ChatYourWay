package com.chat.yourway.exception;

public class MessagePermissionDeniedException extends RuntimeException {
    public MessagePermissionDeniedException(String message) {
        super(message);
    }
}
