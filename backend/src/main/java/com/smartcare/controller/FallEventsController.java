package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
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
@RequestMapping("/fallEvents")
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


}