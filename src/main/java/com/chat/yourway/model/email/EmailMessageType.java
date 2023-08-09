package com.chat.yourway.model.email;

public enum EmailMessageType {
    ACTIVATE("activate");

    private final String emailType;

    EmailMessageType(String emailType) {
        this.emailType = emailType;
    }

    public String getEmailType() {
        return emailType;
    }
}
