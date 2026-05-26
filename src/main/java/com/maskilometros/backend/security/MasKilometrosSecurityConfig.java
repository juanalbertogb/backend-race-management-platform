package com.maskilometros.backend.security;

import com.maskilometros.backend.dto.RoleEnum;
import com.maskilometros.backend.security.authorization.AuthorizationRule;
import com.maskilometros.backend.security.filter.JwtTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import java.util.List;

@Configuration
@EnableWebSecurity  /*opcional: Spring Boot ya activa automáticamente @EnableWebSecurity
                    mediante auto-configuración*/
@RequiredArgsConstructor
public class MasKilometrosSecurityConfig {

    @Qualifier("rulePaths")
    private final List<AuthorizationRule> rulePaths;

    @Qualifier("securedPaths")
    private final List<String> securedPaths;

    private final JwtTokenValidatorFilter jwtTokenValidatorFilter;

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http){
        return http
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests((requests) -> {
                    rulePaths.forEach(rule -> {
                        if(rule.roles().isEmpty()){
                            requests.requestMatchers(rule.httpMethod(),rule.path()).permitAll();
                        }else {
                            String[] roles = rule.roles().stream().map(RoleEnum::name).toArray(String[]::new);
                            requests.requestMatchers(rule.httpMethod(), rule.path()).hasAnyRole(roles);
                        }
                    });

                    securedPaths.forEach(path -> requests.requestMatchers(path).authenticated());
                    requests.anyRequest().denyAll();
                })
                .addFilterBefore(jwtTokenValidatorFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(fL -> fL.disable())
//                .httpBasic(Customizer.withDefaults())
                .httpBasic(hB -> hB.disable())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider){
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
