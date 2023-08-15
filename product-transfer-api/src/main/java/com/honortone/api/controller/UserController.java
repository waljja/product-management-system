package com.honortone.api.controller;

import cn.hutool.core.util.StrUtil;
import com.honortone.api.common.Constants;
import com.honortone.api.common.Result;
import com.honortone.api.controller.dto.IUserDto;
import com.honortone.api.entity.IUser;
import com.honortone.api.service.IUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 登录接口
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */

@Slf4j
@Controller
@Api("用户登录接口")
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private IUserService userService;

   /* @ResponseBody
    @PostMapping("/login")
    public Result login(@RequestBody IUserDto iUserDto, HttpSession session) {
        System.out.println(iUserDto.getUsername());
        String username = iUserDto.getUsername();
        String password = iUserDto.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400,"参数错误");
        }
        IUserDto dto = userService.login(iUserDto);
        session.setAttribute("username",dto.getUsername());
        session.setAttribute("password",dto.getPassword());
        return Result.success(dto);
    }*/

    /**
     * 登录
     */
   @ResponseBody
   @PostMapping(value = "/login")
   public Result login(@RequestBody IUserDto iUserDto, HttpServletRequest request) {
       System.out.println(iUserDto.getUsername());
       String username = iUserDto.getUsername();
       String password = iUserDto.getPassword();
       if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
           return Result.error(Constants.CODE_400,"参数错误");
       }
       IUserDto dto = userService.login(iUserDto, request);
       if(dto.getUsername().equals("密码错误")){
           return Result.error(Constants.CODE_400,"密码错误");
       }else if(dto.getUsername().equals("不存在用户")){
           return Result.error(Constants.CODE_400,"不存在用户");
       }else {
//           dto.setUsername("");
//           dto.setPassword("");
           return Result.success(dto);
       }
       /*session.setAttribute("username",dto.getUsername());
       session.setAttribute("password",dto.getPassword());*/

   }

    @ResponseBody
    @PostMapping(value = "/register")
    public Result register(@RequestBody IUser iUser) {
        if (iUser.getUsername() == null || iUser.getPassword() == null) {
            return Result.error("600","账号/密码不能为空");
        }
        IUser iUser1 =  userService.register(iUser);
        if (iUser1.getUsername().toString().equals("该用户已存在")) {
            return Result.error("600","已存在用户");
        }
        return Result.success();
    }
}
