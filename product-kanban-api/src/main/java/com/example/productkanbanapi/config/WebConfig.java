package com.example.productkanbanapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 *
 * @author 丁国钊
 * @date 2023-1-29
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        // 设置允许跨域的路由
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 允许请求头
                .allowedHeaders("*")
                // 设置允许的方法
                .allowedMethods("POST", "GET", "PUT", "DELETE")
                // 再次加入前端Origin
                .allowedOrigins("http://localhost")
                // 是否允许证书（cookies）
                .allowCredentials(true);
    }

}

