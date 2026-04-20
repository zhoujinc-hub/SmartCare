package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.alert.AlertLogQueryDto;
import com.smartcare.dto.alert.HandleAlertDto;
import com.smartcare.service.AlertLogsService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.alert.AlertLogVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alertLogs")
public class AlertLogsController {

    private final AlertLogsService alertLogsService;

    @PostMapping("/list")
    public Result<PageVo<AlertLogVo>> list(@RequestBody AlertLogQueryDto dto) {
        return Result.ok(alertLogsService.list(dto));
    }

    @GetMapping("/{alertId}")
    public Result<AlertLogVo> detail(@PathVariable Long alertId) {
        return Result.ok(alertLogsService.detail(alertId));
    }

    @PutMapping("/handle")
    public Result<Void> handle(@RequestBody HandleAlertDto dto) {
        alertLogsService.handle(dto);
        return Result.ok();
    }

    @PostMapping("/{alertId}/resend")
    public Result<Void> resend(@PathVariable Long alertId) {
        alertLogsService.resend(alertId);
        return Result.ok();
    }
}