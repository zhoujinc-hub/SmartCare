package com.smartcare.vo.elder;

import com.smartcare.dto.elder.ElderRelativeDto;
import lombok.Data;

import java.util.List;

@Data
public class ElderDetailVo {
    private Long elderId;
    private String name;
    private Integer age;
    private Byte gender;
    private String address;
    private String healthNotes;
    private List<ElderRelativeDto> relatives;
}