package com.honortone.api.service.impl;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honortone.api.common.Constants;
import com.honortone.api.controller.dto.IUserDto;
import com.honortone.api.exception.ServiceException;
import com.honortone.api.mapper.IUserMapper;
import com.honortone.api.service.IUserService;
import com.honortone.api.utils.PasswordUtils;
import com.honortone.api.utils.TokenUtils;
import com.honortone.api.entity.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *     登录/用户接口实现类
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */

@Service
public class IUserServiceImpl extends ServiceImpl<IUserMapper,IUser> implements IUserService {

    private static final Log LOG = Log.get();

    @Autowired
    private IUserMapper iUserMapper;
    @Override
    public IUserDto login(IUserDto iUserDto, HttpServletRequest request) {

        IUser iUser = iUserMapper.selectOne(Wrappers.<IUser>lambdaQuery().eq(IUser::getUsername, iUserDto.getUsername()));
        System.out.println("sss" + iUserDto.getUsername());
        //System.out.println(iUser.getSalt());  若iUser为空的话，这行打印语句会报错
        if (iUser != null) {

            System.out.println(iUser.getSalt() + "jtm" + iUserDto.getPassword() + "jtm" + iUser.getPassword());
            if (PasswordUtils.matches(iUser.getSalt(), iUserDto.getPassword(), iUser.getPassword())) {

                // 设置token
                String token = TokenUtils.genToken(iUserDto.getUsername(), iUserDto.getPassword());
                String rple = iUserMapper.checkRole(iUserDto.getUsername().toString(), iUser.getPassword().toString()) == null ? "" : iUserMapper.checkRole(iUserDto.getUsername().toString(), iUser.getPassword().toString());
                iUserDto.setToken(token);
                iUserDto.setRole(rple);
                System.out.println("1111" + iUserDto.getToken().toString());
            } else {
                // throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
                iUserDto.setUsername("密码错误");
                return iUserDto;
            }
        } else {
            iUserDto.setUsername("不存在用户");
            return iUserDto;
        }

        return iUserDto;
    }

    @Override
    public IUser register(IUser iUser) {
        IUser iUser1 = iUserMapper.selectOne(Wrappers.<IUser>lambdaQuery().eq(IUser::getUsername, iUser.getUsername()));
        if (iUser1 != null) {
            iUser.setUsername("该用户已存在");
        } else {
            iUser.setSalt(PasswordUtils.getSalt());
            String encode = PasswordUtils.encode(iUser.getPassword(), iUser.getSalt());
            iUser.setPassword(encode);
            iUserMapper.insert(iUser);
        }

        return iUser;
    }

    private IUser getUserInfo(IUserDto iUserDto) {
        QueryWrapper<IUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", iUserDto.getUsername());
        queryWrapper.eq("password", iUserDto.getPassword());
        IUser one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }
}
