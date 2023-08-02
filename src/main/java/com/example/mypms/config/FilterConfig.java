package com.example.mypms.config;


import com.example.mypms.filter.LogFilter;
import com.example.mypms.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Autowired
    LogService logService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter(logService));
        registration.addUrlPatterns("/*");
        registration.setName("LogFilter");
        registration.setOrder(1);
        return registration;
    }
}
