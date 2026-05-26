package com.maskilometros.backend.auth.service;

import com.maskilometros.backend.dto.LoginRequestDto;
import com.maskilometros.backend.dto.LoginResponseDto;
import com.maskilometros.backend.dto.RegisterRequestDto;
import com.maskilometros.backend.dto.RegisterResponseDto;

public interface IAuthService {

    LoginResponseDto login(LoginRequestDto requestDto);

    RegisterResponseDto register(RegisterRequestDto requestDto);
}
