package com.chat.yourway.dto.common;

public record EmailMessageDto(String to,
                              String subject,
                              String text
) {
}

