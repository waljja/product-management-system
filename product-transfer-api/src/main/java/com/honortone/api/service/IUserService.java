package com.honortone.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honortone.api.controller.dto.IUserDto;
import com.honortone.api.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService extends IService<IUser> {

    public IUserDto login(IUserDto iUserDto, HttpServletRequest request);

    IUser register(IUser iUser);
}
