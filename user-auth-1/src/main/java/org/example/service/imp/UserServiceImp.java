package org.example.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.JwtTokenProvider;
import org.example.model.UserRepository;
import org.example.model.entity.UserEntity;
import org.example.model.mapper.UserMapper;
import org.example.model.req.LoginRequest;
import org.example.model.req.UserRequest;
import org.example.model.req.UserUpdateRequest;
import org.example.model.res.JwtAuthenticationResponse;
import org.example.model.res.UserRes;
import org.example.util.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImp {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Resource
    private UserMapper userMapper;

    private final AuthenticationFacade authenticationFacade;

    private final UserRepository userRepository;

    public JwtAuthenticationResponse logIn(LoginRequest loginRequest) {
        // 1 find user in db
        Optional<UserEntity> optionalUserEntity = Optional.ofNullable(userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("Not Found")));
        UserEntity userEntity = optionalUserEntity.get();
        log.info("userEntity: {}", userEntity);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            // Handle wrong password error here
            log.warn("password not match");
            userEntity.setFailLoginAttempt(userEntity.getFailLoginAttempt()+1);
            userRepository.save(userEntity);
            throw new RuntimeException(e.getMessage());
            //      log.warn("Wrong password entered for username: " + loginRequest.getUsername());
        } catch (Exception e) {
            // Handle other authentication errors here
            log.warn("Authentication failed for username: " + loginRequest.getUsername() + ", reason: " + e.getMessage());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        User user = (User) authentication.getPrincipal();
//        log.info("user {}", user);
//        String jwt = jwtTokenProvider.generateToken(authentication);


        // 2 check pass match
        if (passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
            UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            log.info("userDetails authorities {}", user.getAuthorities());

            // generate access token
            String accessToken = jwtTokenProvider.generateToken(user);
            jwtTokenProvider.getExpirationDate(accessToken);
            log.info("access token: {}", accessToken.substring(10, 16));

            // generate refresh token
            String refreshTk = jwtTokenProvider.generateRefreshToken();
            log.info("refresh token: {}", refreshTk.subSequence(33, 39));
            jwtTokenProvider.getExpirationDate(refreshTk);

            // update user db
            userEntity.setAccessToken(accessToken);
            userEntity.setRefreshToken(refreshTk);
            userEntity.setFailLoginAttempt(0);
            userRepository.save(userEntity);

            JwtAuthenticationResponse response = new JwtAuthenticationResponse();

            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshTk);

            response.setUserRes(userMapper.entityToRes(optionalUserEntity.get()));
            return response;
        } else {
            userEntity.setFailLoginAttempt(userEntity.getFailLoginAttempt() + 1);
            userRepository.save(userEntity);
            throw new RuntimeException("pass not match");
        }
        //set res
    }

    public UserRes getByUsername(String username) {
        var userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            var userRes = userMapper.entityToRes(userEntity.get());
            return userRes;
        } else return new UserRes();
    }

    public void create(UserRequest req) {
        // find username in db
        var userEntityOptional = userRepository.findByUsername(req.getUsername());

        if (!userEntityOptional.isPresent()) {
            // encrypt pass
            req.setPassword(passwordEncoder.encode(req.getPassword()));
            UserEntity userEntity = userMapper.req2Entity(req);
            userEntity.setFailLoginAttempt(0);
            userEntity.setCreatedAt(LocalDateTime.now());
            // save entity
            userRepository.save(userEntity);
        } else throw new RuntimeException("user exist");
    }

    public List<UserRes> listAll() {
        var findAll = userRepository.findAll();

        List<UserRes> res = new ArrayList<>();
        findAll.forEach(r-> res.add(userMapper.entityToRes(r)));
        return res;
    }

    public void logOut() {
        UserEntity userEntity = authenticationFacade.getUserEntity();
        userEntity.setRefreshToken("");
        userEntity.setAccessToken("");
        userRepository.save(userEntity);
    }

    public UserRes getById(long id) {
        // find in db
        var user = userRepository.findById(id);
        log.info("user: {}", user);
        if (user.isPresent()){
            var userEntity = user.get();
            var userRes = userMapper.entityToRes(userEntity);
            return userRes;
        } else throw new RuntimeException("Not Found");
    }

    public void updateByid(long id, UserUpdateRequest updateRequest) {
        // find user
        var user = userRepository.findById(id);
        if (user.isPresent()){
            log.info("user: {}", user);
            var userEntity = user.get();
            userEntity.setUpdatedAt(LocalDateTime.now());
            // role update
            if (!updateRequest.getRoles().isEmpty()){
                userEntity.setRoles(userMapper.roleReq2RoleEntity(updateRequest.getRoles()));
            }
            // save
            userRepository.save(userEntity);
            log.info("user updated: {}" , userEntity);
        }
    }

    public void deleteByid(long id) {
        // find
        if (id == authenticationFacade.getUserEntity().getId()) throw new RuntimeException("Can't delete ourself");
        userRepository.deleteById(id);
    }
}
