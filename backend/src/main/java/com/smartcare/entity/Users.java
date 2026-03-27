package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户表")
@TableName("users")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Users extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    private String password;

    @Schema(description = "真实姓名")
    @TableField("real_name")
    private String realName;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "用户类型 1:管理员 2:家属")
    @TableField("user_type")
    private Byte userType;

    @Schema(description = "状态 0:禁用 1:启用")
    @TableField("status")
    private Byte status;
}