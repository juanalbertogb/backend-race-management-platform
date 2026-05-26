package com.maskilometros.backend.auth.controller;

import com.maskilometros.backend.auth.service.IAuthService;
import com.maskilometros.backend.dto.LoginRequestDto;
import com.maskilometros.backend.dto.LoginResponseDto;
import com.maskilometros.backend.dto.RegisterRequestDto;
import com.maskilometros.backend.dto.RegisterResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {

    public final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto){

        LoginResponseDto responseDto = authService.login(requestDto);

        return ResponseEntity.ok(responseDto);

    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register
            (@RequestBody @Valid RegisterRequestDto masKilometrosUserRequestDto){

        RegisterResponseDto userResponseDto = authService.register(masKilometrosUserRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }
}
