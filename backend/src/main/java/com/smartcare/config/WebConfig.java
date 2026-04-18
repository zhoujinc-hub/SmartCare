package com.smartcare.config;

import com.smartcare.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/api/user/login",
//                        "/doc.html",
//                        "/webjars/**",
//                        "/v3/api-docs/**",
//                        "/swagger-resources/**",
//                        "/swagger-ui/**",
//                        "/favicon.ico"
//                );
//    }
   }
}