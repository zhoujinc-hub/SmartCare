package com.smartcare.dto.elder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 家属端添加老人DTO
 *
 * 注意：
 * 前端传的是 name，不是 realName。
 */
@Data
public class ElderAddDTO {

    /**
     * 老人姓名
     */
    @NotBlank(message = "老人姓名不能为空")
    private String name;

    /**
     * 性别：0女 1男
     */
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @Min(value = 50, message = "年龄不能小于50")
    @Max(value = 120, message = "年龄不能大于120")
    private Integer age;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 健康备注
     */
    private String healthNotes;
}