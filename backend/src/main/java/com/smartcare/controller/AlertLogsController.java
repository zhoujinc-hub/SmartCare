package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.alert.AlertLogQueryDto;
import com.smartcare.service.AlertLogsService;
import com.smartcare.service.FallEventsService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.alert.AlertLogVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alertLogs")
public class AlertLogsController {


    private final AlertLogsService alertLogsService;

    @PostMapping("/list")
    public Result<PageVo<AlertLogVo>> list(@RequestBody AlertLogQueryDto dto) {
        try {
            return Result.ok(alertLogsService.list(dto));
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }
}