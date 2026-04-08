package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.entity.Elders;
import com.smartcare.service.impl.EldersServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/elders")
public class EldersController {

    private final EldersServiceImpl eldersService;


    @PostMapping("/list")
    public Result<PageVo<ElderListVo>> list(@RequestBody ElderQueryDto dto) {
        try {
            return Result.ok(eldersService.list(dto));
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }
}