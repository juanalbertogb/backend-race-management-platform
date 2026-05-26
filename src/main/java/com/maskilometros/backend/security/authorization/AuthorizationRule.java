package com.maskilometros.backend.security.authorization;

import com.maskilometros.backend.dto.RoleEnum;
import org.springframework.http.HttpMethod;

import java.util.List;

public record AuthorizationRule(
        HttpMethod httpMethod,
        String path,
        List<RoleEnum> roles
) {
}
