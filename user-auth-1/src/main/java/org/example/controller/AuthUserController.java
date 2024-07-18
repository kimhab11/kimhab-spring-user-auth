package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.JwtTokenProvider;
import org.example.model.req.UserRequest;
import org.example.model.res.JwtAuthenticationResponse;
import org.example.model.req.LoginRequest;
import org.example.repo.UserRepository;
import org.example.service.imp.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
public class AuthUserController {
    @Autowired()
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private final UserServiceImp userServiceImp;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        JwtAuthenticationResponse response = userServiceImp.logIn(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid UserRequest req) {
        userServiceImp.create(req);
        return ResponseEntity.ok(HttpStatus.CREATED.getReasonPhrase());
    }

    @PostMapping("logout")
    public ResponseEntity<?> logOut(){
        userServiceImp.logOut();
        return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
    }
}
