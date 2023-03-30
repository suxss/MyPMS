package com.example.mypms.filter;

import com.example.mypms.model.User;
import com.example.mypms.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebFilter(filterName = "LogFilter", urlPatterns = {"/*"})
public class LogFilter implements Filter {
    LogService logService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LogFilter(LogService logService) {
        this.logService = logService;
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        String method = ((HttpServletRequest) request).getMethod();
        String path = ((HttpServletRequest) request).getRequestURI();
        String param = ((HttpServletRequest) request).getQueryString();
        String ip = request.getRemoteAddr();
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user");
        String uid = (user == null) ? null : user.getUid();
        this.logService.insertLog(method, path, param, ip, uid);
        logger.info("LogFilter >>  method: " + method + ", path: " + path + ", param: " + param + ", ip: " + ip + ", uid: " + uid);
        chain.doFilter(request, response);
    }
}
