package com.smartcare.dto.elder;

import lombok.Data;

import java.util.List;

@Data
public class ElderSaveDto {
    private String name;
    private Integer age;
    private Byte gender;
    private String address;
    private String healthNotes;
    private List<Long> relativeIds;
}