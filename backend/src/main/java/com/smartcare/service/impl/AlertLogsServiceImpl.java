package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.common.exception.BusinessException;
import com.smartcare.dto.alert.AlertLogQueryDto;
import com.smartcare.dto.alert.HandleAlertDto;
import com.smartcare.entity.AlertLogs;
import com.smartcare.mapper.AlertLogsMapper;
import com.smartcare.service.AlertLogsService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.alert.AlertLogVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertLogsServiceImpl implements AlertLogsService {

    private final AlertLogsMapper alertLogsMapper;

    @Override
    public PageVo<AlertLogVo> list(AlertLogQueryDto dto) {
        LambdaQueryWrapper<AlertLogs> wrapper = new LambdaQueryWrapper<>();

        if (dto.getEventId() != null) {
            wrapper.eq(AlertLogs::getEventId, dto.getEventId());
        }
        if (dto.getSendStatus() != null) {
            wrapper.eq(AlertLogs::getSendStatus, dto.getSendStatus());
        }

        if (dto.getSendMethod() != null) {
            wrapper.eq(AlertLogs::getSendMethod, dto.getSendMethod());
        }

        if (dto.getRecipientType() != null) {
            wrapper.eq(AlertLogs::getRecipientType, dto.getRecipientType());
        }

        if (dto.getStartTime() != null) {
            wrapper.ge(AlertLogs::getSentAt, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(AlertLogs::getSentAt, dto.getEndTime());
        }

        wrapper.orderByDesc(AlertLogs::getAlertId);

        Page<AlertLogs> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<AlertLogs> alertPage = alertLogsMapper.selectPage(page, wrapper);

        List<AlertLogVo> voList = new ArrayList<>();
        for (AlertLogs item : alertPage.getRecords()) {
            AlertLogVo vo = new AlertLogVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        }

        PageVo<AlertLogVo> result = new PageVo<>();
        result.setTotal(alertPage.getTotal());
        result.setPages(alertPage.getPages());
        result.setPageNum(alertPage.getCurrent());
        result.setPageSize(alertPage.getSize());
        result.setRecords(voList);
        return result;
    }

    @Override
    public AlertLogVo detail(Long alertId) {
        AlertLogs log = alertLogsMapper.selectById(alertId);
        if (log == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_EXIST, "告警不存在");
        }

        AlertLogVo vo = new AlertLogVo();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    @Override
    public void handle(HandleAlertDto dto) {
        AlertLogs log = alertLogsMapper.selectById(dto.getAlertId());
        if (log == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_EXIST, "告警不存在");
        }

        log.setSendStatus(dto.getSendStatus());
        // 新增：接收前端传递的 errorMsg 并更新
        if (dto.getErrorMsg() != null) {
            log.setErrorMsg(dto.getErrorMsg());
        }
        alertLogsMapper.updateById(log);
    }

    @Override
    public void resend(Long alertId) {
        AlertLogs log = alertLogsMapper.selectById(alertId);
        if (log == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_EXIST, "告警不存在");
        }

        log.setSendStatus((byte) 1);
        log.setErrorMsg(null);
        alertLogsMapper.updateById(log);
    }
}