package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.fall.FallEventHandleDto;
import com.smartcare.dto.fall.FallEventNotesDto;
import com.smartcare.dto.fall.FallEventQueryDto;
import com.smartcare.entity.FallEvents;
import com.smartcare.service.impl.FallEventsServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.fall.FallEventVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fallEvents")
public class FallEventsController {

    private final FallEventsServiceImpl fallEventsService;


    @PostMapping("/list")
    public Result<PageVo<FallEventVo>> list(@RequestBody FallEventQueryDto dto) {
        try {
            return Result.ok(fallEventsService.list(dto));
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }


    @GetMapping("/detail/{eventId}")
    public Result<FallEventVo> detail(@PathVariable Long eventId) {
        try {
            return Result.ok(fallEventsService.detail(eventId));
        } catch (RuntimeException e) {
            if ("跌倒事件不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.FALL_EVENT_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }


    @PutMapping("/handle/{eventId}")
    public Result<Void> handle(@PathVariable Long eventId,
                               @RequestBody FallEventHandleDto dto) {
        try {
            fallEventsService.handle(eventId, dto);
            return Result.ok();
        } catch (RuntimeException e) {
            if ("跌倒事件不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.FALL_EVENT_NOT_EXIST);
            }
            if ("该告警已处理".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.FALL_EVENT_ALREADY_HANDLED);
            }
            return Result.build(null, ResultCodeEnum.FALL_EVENT_HANDLE_ERROR);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FALL_EVENT_HANDLE_ERROR);
        }
    }


    @PutMapping("/notes/{eventId}")
    public Result<Void> saveNotes(@PathVariable Long eventId,
                                  @RequestBody FallEventNotesDto dto) {
        try {
            fallEventsService.saveNotes(eventId, dto);
            return Result.ok();
        } catch (RuntimeException e) {
            if ("跌倒事件不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.FALL_EVENT_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.FALL_EVENT_NOTE_ERROR);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FALL_EVENT_NOTE_ERROR);
        }
    }


}