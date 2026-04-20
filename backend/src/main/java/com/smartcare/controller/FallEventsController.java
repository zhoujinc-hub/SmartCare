package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.fall.FallEventHandleDto;
import com.smartcare.dto.fall.FallEventNotesDto;
import com.smartcare.dto.fall.FallEventQueryDto;
import com.smartcare.service.impl.FallEventsServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.fall.FallEventVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fallEvents")
public class FallEventsController {

    private final FallEventsServiceImpl fallEventsService;

    @PostMapping("/list")
    public Result<PageVo<FallEventVo>> list(@RequestBody FallEventQueryDto dto) {
        return Result.ok(fallEventsService.list(dto));
    }

    @GetMapping("/detail/{eventId}")
    public Result<FallEventVo> detail(@PathVariable Long eventId) {
        return Result.ok(fallEventsService.detail(eventId));
    }

    @PutMapping("/handle/{eventId}")
    public Result<Void> handle(@PathVariable Long eventId,
                               @RequestBody FallEventHandleDto dto) {
        fallEventsService.handle(eventId, dto);
        return Result.ok();
    }

    @PutMapping("/notes/{eventId}")
    public Result<Void> saveNotes(@PathVariable Long eventId,
                                  @RequestBody FallEventNotesDto dto) {
        fallEventsService.saveNotes(eventId, dto);
        return Result.ok();
    }
}