package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.dto.elder.ElderSaveDto;
import com.smartcare.dto.elder.ElderUpdateDto;
import com.smartcare.entity.Elders;
import com.smartcare.service.impl.EldersServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderDetailVo;
import com.smartcare.vo.elder.ElderListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elders")
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
    @PostMapping("/add")
    public Result<Void> add(@RequestBody ElderSaveDto dto) {
        try {
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                return Result.build(null, ResultCodeEnum.ELDER_NAME_EMPTY);
            }
            eldersService.add(dto);
            return Result.ok();
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.ELDER_SAVE_ERROR);
        }
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody ElderUpdateDto dto) {
        try {
            eldersService.update(dto);
            return Result.build(null, ResultCodeEnum.SUCCESS);
        } catch (RuntimeException e) {
            if ("老人不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.ELDER_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.ELDER_UPDATE_ERROR);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.ELDER_UPDATE_ERROR);
        }
    }

    @DeleteMapping("/delete/{elderId}")
    public Result<Void> delete(@PathVariable Long elderId) {
        try {
            eldersService.delete(elderId);
            return Result.build(null, ResultCodeEnum.SUCCESS);
        } catch (RuntimeException e) {
            if ("老人不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.ELDER_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.ELDER_DELETE_ERROR);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.ELDER_DELETE_ERROR);
        }
    }

    @GetMapping("/detail/{elderId}")
    public Result<ElderDetailVo> detail(@PathVariable Long elderId) {
        try {
            ElderDetailVo vo = eldersService.detail(elderId);
            return Result.build(vo, ResultCodeEnum.SUCCESS);
        } catch (RuntimeException e) {
            if ("老人不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.ELDER_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.FAIL);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }

}