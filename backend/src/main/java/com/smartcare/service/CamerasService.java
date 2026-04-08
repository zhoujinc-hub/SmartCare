package com.smartcare.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcare.dto.camera.CameraQueryDto;
import com.smartcare.entity.Cameras;
import com.smartcare.mapper.CamerasMapper;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.camera.CameraVo;

public interface CamerasService{
    PageVo<CameraVo> list(CameraQueryDto dto);
}