package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Schema(description = "摄像头表")
@TableName("cameras")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Cameras extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "摄像头ID")
    @TableId(value = "camera_id", type = IdType.AUTO)
    private Long cameraId;

    @Schema(description = "摄像头类型 0:家庭 1:社区")
    @TableField("camera_type")
    private Byte cameraType;

    @Schema(description = "摄像头名称")
    @TableField("camera_name")
    private String cameraName;

    @Schema(description = "设备序列号")
    @TableField("device_serial")
    private String deviceSerial;

    @Schema(description = "视频流地址")
    @TableField("stream_url")
    private String streamUrl;

    @Schema(description = "位置描述")
    @TableField("location_desc")
    private String locationDesc;

    @Schema(description = "纬度")
    @TableField("latitude")
    private BigDecimal latitude;

    @Schema(description = "经度")
    @TableField("longitude")
    private BigDecimal longitude;

    @Schema(description = "状态 0:离线 1:在线")
    @TableField("status")
    private Byte status;
}