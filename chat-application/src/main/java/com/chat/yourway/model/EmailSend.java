package com.chat.yourway.model;

public record EmailSend(String to,
                        String subject,
                        String text
) {
}

