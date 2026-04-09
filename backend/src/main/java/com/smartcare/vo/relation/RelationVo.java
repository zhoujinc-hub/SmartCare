package com.smartcare.vo.relation;

import lombok.Data;

@Data
public class RelationVo {
    private Long relationId;
    private Long elderId;
    private Long userId;
    private String relationship;
    private String realName;
    private String phone;
}