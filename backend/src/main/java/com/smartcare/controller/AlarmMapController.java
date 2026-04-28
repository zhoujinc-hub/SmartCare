package com.smartcare.controller;

import com.smartcare.service.AlarmMapService;
import com.smartcare.vo.AlarmMapVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "报警地图")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm-map")
public class AlarmMapController {

    private final AlarmMapService alarmMapService;

    @Operation(summary = "查询报警地图点位")
    @GetMapping("/list")
    public List<AlarmMapVO> list(@RequestParam(required = false) Byte status) {
        return alarmMapService.listAlarmMap(status);
    }

    @Operation(summary = "更新报警事件状态")
    @PutMapping("/{eventId}/status")
    public Boolean updateStatus(@PathVariable Long eventId,
                                @RequestBody UpdateStatusRequest request) {
        alarmMapService.updateStatus(eventId, request.getStatus(), request.getProcessedBy(), request.getProcessNotes());
        return true;
    }

    @Operation(summary = "手动推送新报警，方便联调WebSocket")
    @PostMapping("/{eventId}/push")
    public Boolean pushNewAlarm(@PathVariable Long eventId) {
        alarmMapService.pushNewAlarm(eventId);
        return true;
    }

    @Data
    public static class UpdateStatusRequest {
        private Byte status;
        private Long processedBy;
        private String processNotes;
    }
}
