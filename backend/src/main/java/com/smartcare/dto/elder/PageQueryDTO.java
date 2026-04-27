package com.smartcare.dto.elder;

import lombok.Data;

/**
 * 分页查询DTO
 */
@Data
public class PageQueryDTO {

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * MySQL分页偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}