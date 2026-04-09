package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.camera.CameraQueryDto;
import com.smartcare.entity.Cameras;
import com.smartcare.service.impl.CamerasServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.camera.CameraVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cameras")
public class CamerasController {

    private final CamerasServiceImpl camerasService;

    @GetMapping("/list")
    public Result<PageVo<CameraVo>> list(CameraQueryDto dto) {
        try {
            return Result.ok(camerasService.list(dto));
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }


}