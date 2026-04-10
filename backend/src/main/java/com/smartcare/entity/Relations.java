package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "老人家属关系表")
@TableName("relations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Relations extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关系ID")
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    @Schema(description = "老人ID")
    @TableField("elder_id")
    private Long elderId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "关系，如父子、母女等")
    @TableField("relationship")
    private String relationship;
}