package com.honortone.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honortone.api.entity.IUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */

@Mapper
@Repository
public interface IUserMapper extends BaseMapper<IUser> {

    IUser findUser(String username);

    String checkRole(@Param("username") String username, @Param("password") String password);

}
