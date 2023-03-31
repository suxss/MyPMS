package com.example.mypms.config;

import com.example.mypms.handler.NoAccessDeniedHandler;
import com.example.mypms.handler.UnLoginHandler;
import com.example.mypms.model.Role;
import com.example.mypms.model.User;
import com.example.mypms.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
                        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        PrintWriter out = res.getWriter();
                        Map<String, Object> map = new HashMap();
                        map.put("code", 0);
                        // authentication.getPrincipal() 可以把登录者信息取出来
                        User user = (User) authentication.getPrincipal();
                        HttpSession session = req.getSession();
                        session.setAttribute("user", user);
                        Role role = user.getRole();
                        Map data = Map.of("url", (role.getRid() == 2) ? "v/index.html" : "p/index.html");
                        map.put("data", data);
                        map.put("msg", "登录成功");
//                        map.put("msg", authentication.getPrincipal());
                        out.write(new ObjectMapper().writeValueAsString(map));
                        out.flush();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
                        logger.error("登录失败 >> " + e.getMessage());
                        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        PrintWriter out = res.getWriter();
                        Map<String, Object> map = new HashMap();
                        map.put("code", -1);
                        map.put("msg", e.getMessage());
                        out.write(new ObjectMapper().writeValueAsString(map));
                        out.flush();
                    }
                })
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
                        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        PrintWriter out = res.getWriter();
                        Map<String,Object> map = new HashMap();
                        map.put("code", 0);
                        map.put("msg", "注销登录成功");
                        out.write(new ObjectMapper().writeValueAsString(map));
                        out.flush();
                    }
                })
                .and()
                .userDetailsService(userService);
        return http.build();
    }
}
