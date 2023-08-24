package com.chat.yourway.exception;

public class EmailTokenNotFoundException extends RuntimeException {
    public EmailTokenNotFoundException() {
        super("Current token does not exist");
    }
}
