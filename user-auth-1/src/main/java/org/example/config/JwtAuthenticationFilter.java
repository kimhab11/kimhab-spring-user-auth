package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = jwtTokenProvider.resolveToken(request);

            if (jwt != null && jwtTokenProvider.validateJwtToken(jwt)) {
                log.info("jwt valid");
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                // find accessTk in db
                String dbJwt = userRepository.findByUsername(username).get().getAccessToken();

                if (dbJwt.equals(jwt)){
                    log.info("token db match");
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (dbJwt.isEmpty()) {
                    log.warn("token db empty");
                    throw new RuntimeException("token db empty .");
                } else {
                    log.warn("token invalid");
                    throw new RuntimeException("token invalid .");
                }


            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            // Handle exception
        }

        filterChain.doFilter(request, response);
    }
}
