package com.smartcare.dto.relation;

import lombok.Data;

@Data
public class RelationQueryDto {
    private Long elderId;
    private Long userId;
    private String relationship;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}