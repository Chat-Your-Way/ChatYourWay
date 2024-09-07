package com.chat.yourway.dto.common;

import com.chat.yourway.model.email.EmailMessageType;

import java.util.UUID;

public record EmailMessageInfoDto(String username,
                                  String email,
                                  UUID uuidToken,
                                  String path,
                                  EmailMessageType emailMessageType) { }