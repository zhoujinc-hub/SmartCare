package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.dto.fall.FallEventHandleDto;
import com.smartcare.dto.fall.FallEventNotesDto;
import com.smartcare.dto.fall.FallEventQueryDto;
import com.smartcare.entity.FallEvents;
import com.smartcare.mapper.FallEventsMapper;
import com.smartcare.service.FallEventsService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.fall.FallEventVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class FallEventsServiceImpl implements FallEventsService {

    private final FallEventsMapper fallEventsMapper;

    @Override
    public PageVo<FallEventVo> list(FallEventQueryDto dto) {
        LambdaQueryWrapper<FallEvents> wrapper = new LambdaQueryWrapper<>();

        if (dto.getElderName() != null && !dto.getElderName().trim().isEmpty()) {
            wrapper.like(FallEvents::getElderName, dto.getElderName());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(FallEvents::getStatus, dto.getStatus());
        }
        if (dto.getCameraId() != null) {
            wrapper.eq(FallEvents::getCameraId, dto.getCameraId());
        }

        wrapper.orderByDesc(FallEvents::getEventId);

        Page<FallEvents> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<FallEvents> eventPage = fallEventsMapper.selectPage(page, wrapper);

        List<FallEventVo> voList = new ArrayList<>();
        for (FallEvents item : eventPage.getRecords()) {
            FallEventVo vo = new FallEventVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        }

        PageVo<FallEventVo> result = new PageVo<>();
        result.setTotal(eventPage.getTotal());
        result.setPages(eventPage.getPages());
        result.setPageNum(eventPage.getCurrent());
        result.setPageSize(eventPage.getSize());
        result.setRecords(voList);
        return result;
    }

    @Override
    public FallEventVo detail(Long eventId) {
        return null;
    }

    @Override
    public void handle(Long eventId, FallEventHandleDto dto) {

    }

    @Override
    public void saveNotes(Long eventId, FallEventNotesDto dto) {

    }
}