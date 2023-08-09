package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

  AuthResponseDto register(ContactRequestDto contactRequestDto, HttpServletRequest request);

  AuthResponseDto authenticate(AuthRequestDto authRequestDto);

  AuthResponseDto refreshToken(HttpServletRequest request);
}
