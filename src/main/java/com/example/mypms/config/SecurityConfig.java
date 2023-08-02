package com.example.mypms.config;

import com.example.mypms.handler.*;
import com.example.mypms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {
    @Autowired
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().frameOptions().sameOrigin().and()
                .authorizeRequests(auth ->
                        auth.antMatchers("/p/**").hasRole("p")
                                .antMatchers("/v/**").hasRole("v")
                                .antMatchers("/login.html").permitAll()
                                .antMatchers("/p_register*").permitAll()
                                .antMatchers("/v_register*").permitAll()
                                .antMatchers("/css/**").permitAll()
                                .antMatchers("/js/**").permitAll()
                                .antMatchers("/layui/**").permitAll()
                                .antMatchers("/pic/**").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling().authenticationEntryPoint(new UnLoginHandler())
                .and()
                .exceptionHandling().accessDeniedHandler(new NoAccessDeniedHandler())
                .and()
                .formLogin().permitAll()
                .usernameParameter("email").passwordParameter("password")
                .successHandler(new LoginSuccessHandler())
                .failureHandler(new LoginFailureHandler())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutHandler())
                .and()
                .userDetailsService(userService);
        return http.build();
    }
}
