package com.smartcare.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcare.dto.fall.FallEventHandleDto;
import com.smartcare.dto.fall.FallEventNotesDto;
import com.smartcare.dto.fall.FallEventQueryDto;
import com.smartcare.entity.FallEvents;
import com.smartcare.mapper.FallEventsMapper;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.fall.FallEventVo;

public interface FallEventsService{
    PageVo<FallEventVo> list(FallEventQueryDto dto);

    FallEventVo detail(Long eventId);

    void handle(Long eventId, FallEventHandleDto dto);

    void saveNotes(Long eventId, FallEventNotesDto dto);
}