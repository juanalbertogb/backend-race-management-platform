package com.maskilometros.backend.security;

import com.maskilometros.backend.dto.RoleEnum;
import com.maskilometros.backend.security.authorization.AuthorizationRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class PathsConfig {

    private final static List<RoleEnum> listEmpty = List.of();
    private final static List<RoleEnum> listAuth = List.of(RoleEnum.ORGANIZER, RoleEnum.ADMIN);
    private final static List<RoleEnum> listAll = List.of(RoleEnum.USER, RoleEnum.ORGANIZER, RoleEnum.ADMIN);

    @Bean(name = "rulePaths")
    public List<AuthorizationRule> rulePaths(){
        return List.of(
                //PUBLICAS
                new AuthorizationRule(HttpMethod.POST,"/api/auth/login",listEmpty),
                new AuthorizationRule(HttpMethod.POST,"/api/auth/register",listEmpty),
                new AuthorizationRule(HttpMethod.GET,"/api/races",listEmpty),
                new AuthorizationRule(HttpMethod.GET,"/api/races/*",listEmpty),
                new AuthorizationRule(HttpMethod.GET,"/api/races/available",listEmpty),
                //AUTHORIZATION
                //RaceController
                new AuthorizationRule(HttpMethod.GET,"/api/races/*/registration",listAuth),
                new AuthorizationRule(HttpMethod.POST,"/api/races",listAuth),
                new AuthorizationRule(HttpMethod.PUT,"/api/races/*",listAuth),
                new AuthorizationRule(HttpMethod.DELETE,"/api/races/*",listAuth),
                new AuthorizationRule(HttpMethod.PATCH,"/api/races/*/publish",listAuth),
                new AuthorizationRule(HttpMethod.PATCH,"/api/races/*/close",listAuth),
                new AuthorizationRule(HttpMethod.PATCH,"/api/races/*/cancel",listAuth),
                //RegistrationController
                new AuthorizationRule(HttpMethod.GET,"/api/registrations", listAll),
                new AuthorizationRule(HttpMethod.POST,"/api/registrations/*/register", listAll),
                new AuthorizationRule(HttpMethod.POST,"/api/registrations/*/cancel", listAll),
                //PaymentController
                new AuthorizationRule(HttpMethod.POST,"/api/payments/*/complete", listAll),
                new AuthorizationRule(HttpMethod.POST,"/api/payments/*/retry", listAll)
                );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths(){
        return List.of(
                "/api/**"
        );
    }
}
