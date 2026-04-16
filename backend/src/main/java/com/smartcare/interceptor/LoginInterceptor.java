package com.smartcare.interceptor;

import com.smartcare.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String token = request.getHeader("token");

        if (token == null || !token.startsWith("token_")) {
            throw new RuntimeException("未登录");
        }

        Long userId = Long.parseLong(token.replace("token_", ""));
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.clear();
    }
}