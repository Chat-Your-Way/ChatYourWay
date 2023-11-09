package com.chat.yourway.exception;

public class ContactEmailNotExist extends RuntimeException {
    public ContactEmailNotExist(String message) {
        super(message);
    }
}
