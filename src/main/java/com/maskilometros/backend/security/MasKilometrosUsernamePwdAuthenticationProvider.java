package com.maskilometros.backend.security;

import com.maskilometros.backend.entity.MasKilometrosUser;
import com.maskilometros.backend.repository.MasKilometrosUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MasKilometrosUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    public final MasKilometrosUserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        MasKilometrosUser user = userRepository.fetchUserWithRoleByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: "+email));

        if(passwordEncoder.matches(pwd, user.getPasswordHash())){

            if(!user.isEnabled()){
               throw new DisabledException("User account is disabled");
            }

            List<SimpleGrantedAuthority> authorities =List.of(new SimpleGrantedAuthority(user.getRole().getName()));
            return new UsernamePasswordAuthenticationToken(user, null, authorities);

        }else{
            throw new BadCredentialsException("Invalid Password");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return(UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
