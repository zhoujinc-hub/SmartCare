package com.smartcare.dto.elder;

import lombok.Data;

@Data
public class ElderQueryDto {
    private String name;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}