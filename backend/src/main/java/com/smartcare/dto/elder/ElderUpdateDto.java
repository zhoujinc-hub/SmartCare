package com.smartcare.dto.elder;

import lombok.Data;

@Data
public class ElderUpdateDto {

    private Long elderId;
    private String name;
    private Integer age;
    private Byte gender;
    private String address;

    /**
     * 联系人1
     */
    private String familyContact1;

    /**
     * 电话1
     */
    private String familyPhone1;

    /**
     * 联系人2
     */
    private String familyContact2;

    /**
     * 电话2
     */
    private String familyPhone2;

    /**
     * 身体条件备注
     */
    private String physicalNotes;
}