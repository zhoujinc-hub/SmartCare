package com.smartcare.vo.elder;

import lombok.Data;

@Data
public class ElderListVo {
    private Long elderId;
    private String name;
    private Integer age;
    private Byte gender;
    private String address;
    private String healthNotes;
}