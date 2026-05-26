package com.maskilometros.backend.auth.service.impl;

import com.maskilometros.backend.auth.service.IAuthService;
import com.maskilometros.backend.constants.ApplicationConstants;
import com.maskilometros.backend.dto.*;
import com.maskilometros.backend.entity.MasKilometrosUser;
import com.maskilometros.backend.entity.Role;
import com.maskilometros.backend.exception.ResourceAlreadyExistsException;
import com.maskilometros.backend.exception.ResourceNotFoundException;
import com.maskilometros.backend.repository.MasKilometrosUserRepository;
import com.maskilometros.backend.repository.RoleRepository;
import com.maskilometros.backend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements IAuthService {

    public final JwtUtil jwtUtil;
    public final AuthenticationManager authenticationManager;
    public final MasKilometrosUserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    public final RoleRepository roleRepository;

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        var resultAuthentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(requestDto.email(),
                requestDto.password()));

        //Generate JwtToken
        String jwtToken = jwtUtil.generateToken(resultAuthentication);

        var loggedInUser = (MasKilometrosUser) resultAuthentication.getPrincipal();
        UserDto userDto = transformToUserDto(loggedInUser);

        return new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), userDto, jwtToken);
    }

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto requestDto) {

        if(userRepository.existsByEmail(requestDto.email())){
           throw new ResourceAlreadyExistsException("Email already exists");
        }

        MasKilometrosUser user = transformToEntity(requestDto);
        MasKilometrosUser userSaved = userRepository.save(user);

        return transformToDto(userSaved);
    }

    private MasKilometrosUser transformToEntity(RegisterRequestDto requestDto){
        MasKilometrosUser user = new MasKilometrosUser();
        user.setName(requestDto.name());
        user.setEmail(requestDto.email());
        user.setMobileNumber(requestDto.mobileNumber());

        Role role = roleRepository.findRoleByName(ApplicationConstants.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: "+ApplicationConstants.ROLE_USER));
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(requestDto.password()));
        user.setEnabled(true);
        return user;
    }

    private RegisterResponseDto transformToDto(MasKilometrosUser user){
        return new RegisterResponseDto(user.getName(),user.getEmail(),user.getMobileNumber(),user.getRole(),
                user.getCreatedAt());
    }

    private UserDto transformToUserDto(MasKilometrosUser loggedInUser){
        UserDto userDto = new UserDto();
        userDto.setName(loggedInUser.getName());
        userDto.setEmail(loggedInUser.getEmail());
        userDto.setMobileNumber(loggedInUser.getMobileNumber());
        userDto.setCreatedAt(loggedInUser.getCreatedAt());

        userDto.setRole(loggedInUser.getRole().getName());
        userDto.setUserId(loggedInUser.getId());

        return userDto;
    }


}
