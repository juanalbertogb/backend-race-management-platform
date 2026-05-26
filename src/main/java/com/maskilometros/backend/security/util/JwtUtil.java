package com.maskilometros.backend.security.util;

import com.maskilometros.backend.constants.ApplicationConstants;
import com.maskilometros.backend.entity.MasKilometrosUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@PropertySource(value = "classpath:jwt.properties")
public class JwtUtil {

    public final Environment env;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.subject}")
    private String jwtSubject;

    @Value("${jwt.expiration.hours}")
    private int jwtExpHours;

    @Value("${jwt.prod.expiration.hours}")
    private int jwtProdExpHours;

    @Value("${jwt.secret}")
    public String jwtSecret;

    public String generateToken(Authentication authentication) {
        String jwtToken;

        int expirationHours = jwtExpHours;
        List<String> profiles = Arrays.asList(env.getActiveProfiles());
        if (profiles.contains("prod")) {
            expirationHours = jwtProdExpHours;
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        var fetchedUser = (MasKilometrosUser) authentication.getPrincipal();

        jwtToken = Jwts.builder().issuer(jwtIssuer).subject(jwtSubject)
                .claim("name", fetchedUser.getName())
                .claim("email", fetchedUser.getEmail())
                .claim("mobileNumber", fetchedUser.getMobileNumber())
                .claim("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expirationHours * 60 * 60 * 1000))
                .signWith(secretKey).compact();

        return jwtToken;
    }

    public Claims validateToken(String jwt) {
        return Jwts.parser().verifyWith(getSigningKey())
                .build().parseSignedClaims(jwt).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
