package com.example.mypms.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter out = res.getWriter();
        Map<String, Object> map = new HashMap();
        map.put("code", -1);
        map.put("msg", e.getMessage());
        out.write(new ObjectMapper().writeValueAsString(map));
        out.flush();
    }
}
