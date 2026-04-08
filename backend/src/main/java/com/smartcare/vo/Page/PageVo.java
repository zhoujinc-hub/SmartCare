package com.smartcare.vo.Page;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    private Long total;
    private Long pages;
    private Long pageNum;
    private Long pageSize;
    private List<T> records;
}
