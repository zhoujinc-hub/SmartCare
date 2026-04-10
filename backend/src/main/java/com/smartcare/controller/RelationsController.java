package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.relation.RelationQueryDto;
import com.smartcare.service.impl.RelationsServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.relation.RelationVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/relations")
public class RelationsController {

    private final RelationsServiceImpl relationsService;

    @PostMapping("/list")
    public Result<PageVo<RelationVo>> list(@RequestBody RelationQueryDto dto) {
        try {
            return Result.ok(relationsService.list(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }
}