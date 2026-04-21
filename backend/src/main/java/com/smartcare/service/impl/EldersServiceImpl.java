package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.common.exception.BusinessException;
import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.dto.elder.ElderRelativeDto;
import com.smartcare.dto.elder.ElderSaveDto;
import com.smartcare.dto.elder.ElderUpdateDto;
import com.smartcare.entity.Elders;
import com.smartcare.entity.Relations;
import com.smartcare.entity.Users;
import com.smartcare.mapper.EldersMapper;
import com.smartcare.mapper.RelationsMapper;
import com.smartcare.mapper.UsersMapper;
import com.smartcare.service.EldersService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderDetailVo;
import com.smartcare.vo.elder.ElderListVo;
import com.smartcare.vo.elder.ElderRelativeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EldersServiceImpl implements EldersService {

    private final EldersMapper eldersMapper;
    private final RelationsMapper relationsMapper;
    private final UsersMapper usersMapper;

    @Override
    public PageVo<ElderListVo> list(ElderQueryDto dto) {
        LambdaQueryWrapper<Elders> wrapper = new LambdaQueryWrapper<>();
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            wrapper.like(Elders::getName, dto.getName());
        }
        wrapper.orderByDesc(Elders::getElderId);

        Page<Elders> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Elders> elderPage = eldersMapper.selectPage(page, wrapper);

        List<ElderListVo> voList = new ArrayList<>();
        for (Elders elder : elderPage.getRecords()) {
            ElderListVo vo = new ElderListVo();
            BeanUtils.copyProperties(elder, vo);
            voList.add(vo);
        }

        PageVo<ElderListVo> result = new PageVo<>();
        result.setTotal(elderPage.getTotal());
        result.setPages(elderPage.getPages());
        result.setPageNum(elderPage.getCurrent());
        result.setPageSize(elderPage.getSize());
        result.setRecords(voList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ElderSaveDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException(ResultCodeEnum.ELDER_NAME_EMPTY);
        }

        // 保存老人基本信息
        Elders elder = new Elders();
        BeanUtils.copyProperties(dto, elder);
        eldersMapper.insert(elder);

        // 保存联系人1（和修改接口逻辑一模一样）
        saveSingleContact(
                elder.getElderId(),
                dto.getFamilyContact1(),
                dto.getFamilyPhone1(),
                "联系人1"
        );

        // 保存联系人2
        saveSingleContact(
                elder.getElderId(),
                dto.getFamilyContact2(),
                dto.getFamilyPhone2(),
                "联系人2"
        );
    }

    /**
     * 保存单个联系人
     */
    private void saveSingleContact(Long elderId, String contactName, String phone, String relationship) {
        boolean nameEmpty = contactName == null || contactName.trim().isEmpty();
        boolean phoneEmpty = phone == null || phone.trim().isEmpty();

        // 都没填，直接跳过
        if (nameEmpty && phoneEmpty) {
            return;
        }

        // 一个填了一个没填，报参数错误
        if (nameEmpty || phoneEmpty) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, relationship + "姓名和手机号必须同时填写");
        }

        // 按手机号查用户
        Users user = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getPhone, phone)
                        .last("limit 1")
        );

        if (user == null) {
            // 不能使用全参构造器，必须 new + set
            user = new Users();
            user.setRealName(contactName);
            user.setPhone(phone);
            user.setUsername(phone);
            user.setPassword("123456");
            user.setUserType((byte) 2);
            user.setStatus((byte) 1);
            usersMapper.insert(user);
        } else {
            // 已存在则同步更新联系人姓名
            user.setRealName(contactName);
            usersMapper.updateById(user);
        }

        // 保存 relation
        Relations relation = new Relations();
        relation.setElderId(elderId);
        relation.setUserId(user.getUserId());
        relation.setRelationship(relationship);
        relationsMapper.insert(relation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ElderUpdateDto dto) {
        Elders elder = eldersMapper.selectById(dto.getElderId());
        if (elder == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_NOT_EXIST);
        }

        // 更新老人基本信息
        elder.setName(dto.getName());
        elder.setAge(dto.getAge());
        elder.setGender(dto.getGender());
        elder.setAddress(dto.getAddress());
        elder.setHealthNotes(dto.getPhysicalNotes());
        eldersMapper.updateById(elder);

        // 删除原有联系人关系
        relationsMapper.delete(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, dto.getElderId())
        );

        // 重新保存联系人1
        saveSingleContact(
                dto.getElderId(),
                dto.getFamilyContact1(),
                dto.getFamilyPhone1(),
                "联系人1"
        );

        // 重新保存联系人2
        saveSingleContact(
                dto.getElderId(),
                dto.getFamilyContact2(),
                dto.getFamilyPhone2(),
                "联系人2"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long elderId) {
        Elders elder = eldersMapper.selectById(elderId);
        if (elder == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_NOT_EXIST);
        }

        relationsMapper.delete(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, elderId)
        );
        eldersMapper.deleteById(elderId);
    }

    @Override
    public ElderDetailVo detail(Long elderId) {
        Elders elder = eldersMapper.selectById(elderId);
        if (elder == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_NOT_EXIST);
        }

        ElderDetailVo detailVo = new ElderDetailVo();
        BeanUtils.copyProperties(elder, detailVo);

        List<ElderRelativeVo> relativeList = relationsMapper.selectRelativeListByElderId(elderId);

        if (relativeList != null && !relativeList.isEmpty()) {
            ElderRelativeVo relative1 = relativeList.get(0);
            detailVo.setFamilyContact1(relative1.getRealName());
            detailVo.setFamilyPhone1(relative1.getPhone());
        }

        if (relativeList != null && relativeList.size() > 1) {
            ElderRelativeVo relative2 = relativeList.get(1);
            detailVo.setFamilyContact2(relative2.getRealName());
            detailVo.setFamilyPhone2(relative2.getPhone());
        }

        return detailVo;
    }
}