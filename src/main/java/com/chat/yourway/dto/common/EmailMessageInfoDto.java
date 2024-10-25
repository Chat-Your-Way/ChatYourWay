package com.chat.yourway.dto.common;

import com.chat.yourway.model.enums.EmailMessageType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record EmailMessageInfoDto(String username,
                                  String email,
                                  String path,
                                  EmailMessageType emailMessageType) { }