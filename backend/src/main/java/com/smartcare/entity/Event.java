package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "老人事件记录")
@TableName(value = "events") // 对应数据库表名
@Data
@Builder
public class Event extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联老人ID")
    @TableField(value = "elder_id") // 数据库字段 elder_id 映射为 Java 驼峰命名
    private Long elderId;

    @Schema(description = "事件类型")
    @TableField(value = "event_type") // 数据库字段 event_type 映射为 Java 驼峰命名
    private String eventType;

    @Schema(description = "事件发生时间")
    @TableField(value = "timestamp") // 数据库字段 timestamp 直接映射
    private LocalDateTime timestamp;

}
