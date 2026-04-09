package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.dto.camera.CameraQueryDto;
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
}