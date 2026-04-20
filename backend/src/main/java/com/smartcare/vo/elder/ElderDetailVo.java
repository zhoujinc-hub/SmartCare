package com.smartcare.vo.elder;

import lombok.Data;

@Data
public class ElderDetailVo {

    private Long elderId;
    private String name;
    private Integer gender;
    private Integer age;
    private String address;
    private String familyContact1;
    private String familyPhone1;
    private String familyContact2;
    private String familyPhone2;
    private String physicalNotes;
    private String createdAt;
}