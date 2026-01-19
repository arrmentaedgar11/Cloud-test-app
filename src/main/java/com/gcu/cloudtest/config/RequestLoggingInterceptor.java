package com.gcu.cloudtest.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("Request started: {} {} at {}", request.getMethod(), request.getRequestURI(), LocalDateTime.now());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        if (ex != null) {
            logger.error("Request error: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        }

        logger.info("Request finished: {} {} -> {}", request.getMethod(), request.getRequestURI(), response.getStatus());
    }
}
