package com.smartcare.vo.elder;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 家属端老人信息VO
 *
 * 字段名直接匹配前端 Elder 类型。
 */
@Data
public class ElderVO {

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 当前绑定的家属用户ID
     */
    private Long userId;

    /**
     * 老人姓名
     */
    private String name;

    /**
     * 性别：0女 1男
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 健康备注
     */
    private String healthNotes;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}