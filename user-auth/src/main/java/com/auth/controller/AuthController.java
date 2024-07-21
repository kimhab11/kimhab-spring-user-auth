package com.auth.controller;

import com.auth.entity.RoleEntity;
import com.auth.entity.UserEntity;
import com.auth.model.ERole;
import com.auth.model.req.LoginRequest;
import com.auth.model.res.MessageResponse;
import com.auth.model.res.SignupRequest;
import com.auth.repo.RoleRepository;
import com.auth.service.JwtUtils;
import com.auth.service.NoPasswordCustomUserDetails;
import com.auth.service.UserDetailsImpl;
import com.auth.model.res.JwtResponse;
import com.auth.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signin/nopass") // kimhab
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), ""));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            NoPasswordCustomUserDetails userDetails = (NoPasswordCustomUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("signin/withpass") // kimhab;string
    public ResponseEntity<?> signinWithPass(@Valid @RequestBody LoginRequest loginRequest){
        try {
                    Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(new MessageResponse("Error: Username Pass incorrect"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signUpRequest.getEmail());
        userEntity.setUsername(signUpRequest.getUsername().toLowerCase());

        Set<String> strRoles = signUpRequest.getRole();
        log.info("role req: {}", strRoles);
        Set<RoleEntity> roleEntities = new HashSet<>();

        if (strRoles == null) {
            RoleEntity userRoleEntity = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            roleEntities.add(userRoleEntity);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        RoleEntity adminRoleEntity = roleRepository.findByName(ERole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleEntities.add(adminRoleEntity);

                        break;
                    case "mod":
                        RoleEntity modRoleEntity = roleRepository.findByName(ERole.MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleEntities.add(modRoleEntity);

                        break;
                    default:
                        RoleEntity userRoleEntity = roleRepository.findByName(ERole.USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleEntities.add(userRoleEntity);
                }
            });
        }

        userEntity.setRoleEntities(roleEntities);

        String type = signUpRequest.getType();
        if (type.equalsIgnoreCase("pass")){
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            userEntity.setType(type);
        } else {
            userEntity.setPassword("");
        }
        userRepository.save(userEntity);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
