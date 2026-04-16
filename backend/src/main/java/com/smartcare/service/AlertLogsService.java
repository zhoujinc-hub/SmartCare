package com.smartcare.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcare.dto.alert.AlertLogQueryDto;
import com.smartcare.entity.AlertLogs;
import com.smartcare.mapper.AlertLogsMapper;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.alert.AlertLogVo;

public interface AlertLogsService{
    PageVo<AlertLogVo> list(AlertLogQueryDto dto);
}