package com.smartcare.dto.elder;

import lombok.Data;

@Data
public class ElderSaveDto {
    private String name;
    private Integer age;
    private Byte gender;
    private String address;

    private String physicalNotes;

    private String familyContact1;

    private String familyPhone1;

    private String familyContact2;

    private String familyPhone2;


}