package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.common.exception.BusinessException;
import com.smartcare.dto.camera.CameraQueryDto;
import com.smartcare.dto.camera.CameraSaveDto;
import com.smartcare.dto.camera.CameraUpdateDto;
import com.smartcare.entity.Cameras;
import com.smartcare.mapper.CamerasMapper;
import com.smartcare.service.CamerasService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.camera.CameraVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CamerasServiceImpl implements CamerasService {

    private final CamerasMapper camerasMapper;

    @Override
    public PageVo<CameraVo> list(CameraQueryDto dto) {
        LambdaQueryWrapper<Cameras> wrapper = new LambdaQueryWrapper<>();

        if (dto.getCameraName() != null && !dto.getCameraName().trim().isEmpty()) {
            wrapper.like(Cameras::getCameraName, dto.getCameraName());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Cameras::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(Cameras::getCameraId);

        Page<Cameras> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Cameras> cameraPage = camerasMapper.selectPage(page, wrapper);

        List<CameraVo> voList = new ArrayList<>();
        for (Cameras camera : cameraPage.getRecords()) {
            CameraVo vo = new CameraVo();
            BeanUtils.copyProperties(camera, vo);
            voList.add(vo);
        }

        PageVo<CameraVo> result = new PageVo<>();
        result.setTotal(cameraPage.getTotal());
        result.setPages(cameraPage.getPages());
        result.setPageNum(cameraPage.getCurrent());
        result.setPageSize(cameraPage.getSize());
        result.setRecords(voList);
        return result;
    }

    @Override
    public void add(CameraSaveDto dto) {
        if (dto.getCameraName() == null || dto.getCameraName().trim().isEmpty()) {
            throw new BusinessException(ResultCodeEnum.CAMERA_NAME_EMPTY);
        }
        if (dto.getDeviceSerial() == null || dto.getDeviceSerial().trim().isEmpty()) {
            throw new BusinessException(ResultCodeEnum.DEVICE_SERIAL_EMPTY);
        }

        Cameras camera = new Cameras();
        BeanUtils.copyProperties(dto, camera);
        camerasMapper.insert(camera);
    }

    @Override
    public void update(CameraUpdateDto dto) {
        Cameras camera = camerasMapper.selectById(dto.getCameraId());
        if (camera == null) {
            throw new BusinessException(ResultCodeEnum.CAMERA_NOT_EXIST);
        }

        BeanUtils.copyProperties(dto, camera);
        camerasMapper.updateById(camera);
    }

    @Override
    public void delete(Long cameraId) {
        Cameras camera = camerasMapper.selectById(cameraId);
        if (camera == null) {
            throw new BusinessException(ResultCodeEnum.CAMERA_NOT_EXIST);
        }

        camerasMapper.deleteById(cameraId);
    }

    @Override
    public CameraVo detail(Long cameraId) {
        Cameras camera = camerasMapper.selectById(cameraId);
        if (camera == null) {
            throw new BusinessException(ResultCodeEnum.CAMERA_NOT_EXIST);
        }

        CameraVo vo = new CameraVo();
        BeanUtils.copyProperties(camera, vo);
        return vo;
    }

    @Override
    public void refreshStatus() {
        List<Cameras> list = camerasMapper.selectList(null);

        for (Cameras camera : list) {
            if (camera.getStatus() == null || camera.getStatus() == 0) {
                camera.setStatus((byte) 1);
            } else {
                camera.setStatus((byte) 0);
            }
            camerasMapper.updateById(camera);
        }
    }
}