package com.honortone.api.controller.dto;

import lombok.Data;

/**
 * 接收前端请求参数（字段名与前端参数字段名一致）
 */

@Data   // 该注解相当于同时加上以下注解@Setter @Getter,@ToString,@EqualsAndHashCode
public class IUserDto {

    private String username;
    private String password;

    private String role;
    private String token;
}
