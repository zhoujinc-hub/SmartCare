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

    /**
     * 分页查询摄像头列表
     * 支持按设备名称、摄像头类型、设备状态进行条件筛选
     *
     * @param dto 查询条件DTO，包含分页参数和筛选条件
     * @return 分页结果，包含摄像头VO列表和分页信息
     */
    @Override
    public PageVo<CameraVo> list(CameraQueryDto dto) {
        LambdaQueryWrapper<Cameras> wrapper = new LambdaQueryWrapper<>();

        // 设备名称：没填就不加条件
        if (dto.getCameraName() != null && !dto.getCameraName().trim().isEmpty()) {
            wrapper.like(Cameras::getCameraName, dto.getCameraName().trim());
        }

        // 摄像头类型：没选就不加条件
        if (dto.getCameraType() != null) {
            wrapper.eq(Cameras::getCameraType, dto.getCameraType());
        }

        // 设备状态：没选就不加条件
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

    /**
     * 新增摄像头
     * 校验设备名称和设备序列号不能为空
     *
     * @param dto 摄像头保存DTO，包含摄像头基本信息
     * @throws BusinessException 当设备名称或序列号为空时抛出异常
     */
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

    /**
     * 更新摄像头信息
     * 先校验摄像头是否存在，再执行更新操作
     *
     * @param dto 摄像头更新DTO，包含摄像头ID和需要更新的字段
     * @throws BusinessException 当摄像头不存在时抛出异常
     */
    @Override
    public void update(CameraUpdateDto dto) {
        Cameras camera = camerasMapper.selectById(dto.getCameraId());
        if (camera == null) {
            throw new BusinessException(ResultCodeEnum.CAMERA_NOT_EXIST);
        }

        BeanUtils.copyProperties(dto, camera);
        camerasMapper.updateById(camera);
    }

    /**
     * 删除摄像头
     * 先校验摄像头是否存在，再执行删除操作
     *
     * @param cameraId 摄像头ID
     * @throws BusinessException 当摄像头不存在时抛出异常
     */
    @Override
    public void delete(Long cameraId) {
        Cameras camera = camerasMapper.selectById(cameraId);
        if (camera == null) {
            throw new BusinessException(ResultCodeEnum.CAMERA_NOT_EXIST);
        }

        camerasMapper.deleteById(cameraId);
    }

    /**
     * 查询摄像头详情
     * 根据摄像头ID获取详细信息
     *
     * @param cameraId 摄像头ID
     * @return 摄像头VO对象
     * @throws BusinessException 当摄像头不存在时抛出异常
     */
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

    /**
     * 刷新所有摄像头的在线状态
     * 遍历所有摄像头，将状态取反（0变1，1变0）
     * 注意：此方法目前仅为测试逻辑，实际应调用摄像头SDK检测真实在线状态
     */
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