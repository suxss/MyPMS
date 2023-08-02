package com.example.mypms.handler;

import com.example.mypms.model.Role;
import com.example.mypms.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
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
}
