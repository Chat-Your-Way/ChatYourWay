package com.chat.yourway.model.email;

public record EmailSend(String to,
                        String subject,
                        String text
) {
}

