package com.example.mypms.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LogoutHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter out = res.getWriter();
        Map<String, Object> map = new HashMap();
        map.put("code", 0);
        map.put("msg", "注销登录成功");
        out.write(new ObjectMapper().writeValueAsString(map));
        out.flush();
    }
}
