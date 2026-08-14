package com.rentflow.auth.service;

import com.rentflow.auth.dto.AuthResponse;
import com.rentflow.auth.dto.LoginRequest;
import com.rentflow.auth.dto.RegisterRequest;
import com.rentflow.auth.entity.User;
import com.rentflow.auth.mapper.UserMapper;
import com.rentflow.auth.repository.UserRepository;
import com.rentflow.auth.security.JwtService;
import com.rentflow.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new RuntimeException("Phone number already exists");
        }

        User user=userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser=userRepository.save(user);

        String token=jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        String token=jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}
