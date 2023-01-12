package com.test.app.config;

//import com.test.app.filter.JWTInterceptor;

import com.test.app.util.RogersLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//    private final JWTInterceptor jWTInterceptor;
//
//    public WebConfig(JWTInterceptor jWTInterceptor) {
//        this.jWTInterceptor = jWTInterceptor;
//    }
//
//    @Bean
//    public RogersLogger rogersLogger() {
//        return new RogersLogger();
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jWTInterceptor);
//    }
}
