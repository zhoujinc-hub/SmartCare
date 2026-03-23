package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "老人表")
@TableName("elders")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Elders extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "老人ID")
    @TableId(value = "elder_id", type = IdType.AUTO)
    private Long elderId;

    @Schema(description = "姓名")
    @TableField("name")
    private String name;

    @Schema(description = "年龄")
    @TableField("age")
    private Integer age;

    @Schema(description = "性别 0:女 1:男")
    @TableField("gender")
    private Byte gender;

    @Schema(description = "地址")
    @TableField("address")
    private String address;

    @Schema(description = "健康备注")
    @TableField("health_notes")
    private String healthNotes;
}