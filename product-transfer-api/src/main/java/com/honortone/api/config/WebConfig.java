package com.honortone.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 *
 * @author 江庭明
 * @date 2023-2-9
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

//        1. allowCredentials(true)设置带cookie
//        2：allowedOrigins这里就不能写成allowedOrigins("*")要改成前端Origin记住localhost！=127.0.0.1。

        /*// 添加映射路径
        registry.addMapping("/**").allowedHeaders("*")
                // 是否发送Cookie
                .allowCredentials(true)
                // 设置放行哪些原始域   SpringBoot2.4.4下低版本使用.allowedOrigins("*")
                .allowedOriginPatterns("*")
                // 放行哪些请求方式
                .allowedMethods("*").allowedOrigins("*");*/
    }
}
