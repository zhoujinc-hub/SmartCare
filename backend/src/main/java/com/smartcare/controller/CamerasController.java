package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.camera.CameraQueryDto;
import com.smartcare.dto.camera.CameraSaveDto;
import com.smartcare.dto.camera.CameraUpdateDto;
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
    @PostMapping("/add")
    public Result<Void> add(@RequestBody CameraSaveDto dto) {
        try {
            if (dto.getCameraName() == null || dto.getCameraName().trim().isEmpty()) {
                return Result.build(null, ResultCodeEnum.CAMERA_NAME_EMPTY);
            }
            if (dto.getDeviceSerial() == null || dto.getDeviceSerial().trim().isEmpty()) {
                return Result.build(null, ResultCodeEnum.DEVICE_SERIAL_EMPTY);
            }

            camerasService.add(dto);
            return Result.ok();

        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.CAMERA_SAVE_ERROR);
        }
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody CameraUpdateDto dto) {
        try {
            camerasService.update(dto);
            return Result.build(null, ResultCodeEnum.SUCCESS);

        } catch (RuntimeException e) {
            if ("摄像头不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.CAMERA_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.CAMERA_UPDATE_ERROR);

        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.CAMERA_UPDATE_ERROR);
        }
    }

    @DeleteMapping("/delete/{cameraId}")
    public Result<Void> delete(@PathVariable Long cameraId) {
        try {
            camerasService.delete(cameraId);
            return Result.build(null, ResultCodeEnum.SUCCESS);

        } catch (RuntimeException e) {
            if ("摄像头不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.CAMERA_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.CAMERA_DELETE_ERROR);

        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.CAMERA_DELETE_ERROR);
        }
    }

    @GetMapping("/detail/{cameraId}")
    public Result<CameraVo> detail(@PathVariable Long cameraId) {
        try {
            CameraVo vo = camerasService.detail(cameraId);
            return Result.build(vo, ResultCodeEnum.SUCCESS);

        } catch (RuntimeException e) {
            if ("摄像头不存在".equals(e.getMessage())) {
                return Result.build(null, ResultCodeEnum.CAMERA_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.FAIL);

        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }

    @PostMapping("/refreshStatus")
    public Result<Void> refreshStatus() {
        try {
            camerasService.refreshStatus();
            return Result.build(null, ResultCodeEnum.SUCCESS);

        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.CAMERA_REFRESH_ERROR);
        }
    }


}