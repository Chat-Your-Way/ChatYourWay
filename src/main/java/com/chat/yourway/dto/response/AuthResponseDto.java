package com.chat.yourway.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(String accessToken, String refreshToken) { }