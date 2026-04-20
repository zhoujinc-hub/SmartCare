package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.dto.elder.ElderSaveDto;
import com.smartcare.dto.elder.ElderUpdateDto;
import com.smartcare.service.impl.EldersServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderDetailVo;
import com.smartcare.vo.elder.ElderListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elders")
public class EldersController {

    private final EldersServiceImpl eldersService;

    @PostMapping("/list")
    public Result<PageVo<ElderListVo>> list(@RequestBody ElderQueryDto dto) {
        return Result.ok(eldersService.list(dto));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ElderSaveDto dto) {
        eldersService.add(dto);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody ElderUpdateDto dto) {
        eldersService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/delete/{elderId}")
    public Result<Void> delete(@PathVariable Long elderId) {
        eldersService.delete(elderId);
        return Result.ok();
    }

    @GetMapping("/detail/{elderId}")
    public Result<ElderDetailVo> detail(@PathVariable Long elderId) {
        return Result.ok(eldersService.detail(elderId));
    }
}