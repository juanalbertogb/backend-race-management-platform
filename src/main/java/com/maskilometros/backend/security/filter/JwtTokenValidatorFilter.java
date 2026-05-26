package com.maskilometros.backend.security.filter;

import com.maskilometros.backend.constants.ApplicationConstants;
import com.maskilometros.backend.security.authorization.AuthorizationRule;
import com.maskilometros.backend.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    @Qualifier("rulePaths")
    private final List<AuthorizationRule> rulePaths;

    private final JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(ApplicationConstants.JWT_HEADER);

        if (authHeader != null && authHeader.startsWith(ApplicationConstants.BEARER)) {
            String jwt = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.validateToken(jwt);

                String username = String.valueOf(claims.get("email"));
                String roles = String.valueOf(claims.get("roles"));

                Authentication authentication = new UsernamePasswordAuthenticationToken(username,
                        null, AuthorityUtils.commaSeparatedStringToAuthorityList(roles));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException exception){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token Expired");
                return;
            } catch (JwtException | IllegalArgumentException ex ){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());

        List<AuthorizationRule> publicPath = rulePaths.stream().filter(p -> p.roles().isEmpty())
                .toList();

        return publicPath.stream().anyMatch(pP ->{

                return pathMatcher.match(pP.path(), path) && pP.httpMethod().equals(httpMethod);

        });
    }
}
