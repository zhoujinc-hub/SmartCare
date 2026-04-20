package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.camera.CameraQueryDto;
import com.smartcare.dto.camera.CameraSaveDto;
import com.smartcare.dto.camera.CameraUpdateDto;
import com.smartcare.service.impl.CamerasServiceImpl;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.camera.CameraVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cameras")
public class CamerasController {

    private final CamerasServiceImpl camerasService;

    @GetMapping("/list")
    public Result<PageVo<CameraVo>> list(CameraQueryDto dto) {
        return Result.ok(camerasService.list(dto));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody CameraSaveDto dto) {
        camerasService.add(dto);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody CameraUpdateDto dto) {
        camerasService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/delete/{cameraId}")
    public Result<Void> delete(@PathVariable Long cameraId) {
        camerasService.delete(cameraId);
        return Result.ok();
    }

    @GetMapping("/detail/{cameraId}")
    public Result<CameraVo> detail(@PathVariable Long cameraId) {
        return Result.ok(camerasService.detail(cameraId));
    }

    @PostMapping("/refreshStatus")
    public Result<Void> refreshStatus() {
        camerasService.refreshStatus();
        return Result.ok();
    }
}