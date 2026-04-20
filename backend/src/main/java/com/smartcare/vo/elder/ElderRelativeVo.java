package com.smartcare.vo.elder;

import lombok.Data;

@Data
public class ElderRelativeVo {

    private Long relationId;
    private Long userId;
    private String relationship;
    private String realName;
    private String phone;
}