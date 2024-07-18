package com.kimhab.auth.config;

import com.kimhab.auth.model.ERole;
import com.kimhab.auth.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
//       // securedEnabled = true,
//        // jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthAccessDeny authAccessDeny;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // handle encrypt and decrypt pass
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // set bearer token and OpenApi info
    @Bean
    public OpenAPI customOpenAPI() {
        final String bearerAuth = "bearerAuth";
        var info = new Info()
                .title("Test Open API user auth")
                .version("1.0").contact(new Contact().name("mr kimhab")
                        .email( "kimhab@mail.mail").url("nomail@gmail.com"))
                .description("This is my api");
        var component = new Components()
                .addSecuritySchemes(bearerAuth, new SecurityScheme()
                                .name(bearerAuth)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
        var bearer = new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(bearerAuth))
                .components(component)
                .info(info);
        return bearer;

    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() // enable cors
                .and()
                .csrf().disable() // disable CSRF for simplicity
                .headers().frameOptions().deny()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .exceptionHandling().accessDeniedHandler(authAccessDeny)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/signin").permitAll()
                .antMatchers("/api/auth/signup").hasRole(ERole.ADMIN.name())
                .antMatchers("/api/user/**").hasRole(ERole.ADMIN.name())
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
        //        .and().httpBasic()
//               .and().addFilter(new JwtAuthenticationFilter(authenticationManagerBean()))
        ;

        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //   web.ignoring().antMatchers("/swagger-ui/**", "/bus/v3/api-docs/**");
    }

    /**
     * config CORS with security
    * */
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedOrigin("*"); // specific ip

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }

}
