package com.honortone.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *     用户表数据封装类
 *     变量名需与数据库字段名一致（因使用了@TableName("sys_user")同步数据库表字段），不区分大小写和个数
 * </p>
 *
 * @author 江庭明
 * @since 2023-02-08
 */
@TableName("sys_user") //数据库表名
@ApiModel(value = "IUser对象", description = "")
@Data
public class IUser implements Serializable {


    @TableId
    private String id;

    @ApiModelProperty("账号不能为空")
    private String username;

    private String salt;

    @ApiModelProperty("密码不能为空")
    private String password;

    @ApiModelProperty("用户等级")
    private String role;

    @TableField(exist = false)
    private String oldPwd;

    @TableField(exist = false)
    private String newPwd;

    private String phone;

    private String deptId;

    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private String deptNo;

    private String realName;

    private String nickName;

    private String email;

    private Integer status;

    private Integer sex;

    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    private String createId;

    private String updateId;

    private Integer createWhere;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private String startTime;

    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    private List<String> roleIds;

    @TableField(exist = false)
    private String captcha;

   /* @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "IUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }*/
}
