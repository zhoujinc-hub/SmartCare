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

    // 注入老人信息Mapper，用于数据库操作
    private final EldersMapper eldersMapper;
    // 注入关系Mapper，用于管理老人与家属的关联关系
    private final RelationsMapper relationsMapper;
    // 注入用户Mapper，用于管理家属用户信息
    private final UsersMapper usersMapper;

    /**
     * 分页查询老人列表
     * @param dto 查询条件DTO，包含姓名模糊搜索和分页参数
     * @return 分页结果VO
     */
    @Override
    public PageVo<ElderListVo> list(ElderQueryDto dto) {
        // 构建查询条件
        LambdaQueryWrapper<Elders> wrapper = new LambdaQueryWrapper<>();
        // 如果提供了姓名，进行模糊搜索
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            wrapper.like(Elders::getName, dto.getName());
        }
        // 按老人ID降序排列，最新的在前
        wrapper.orderByDesc(Elders::getElderId);

        // 创建分页对象并执行查询
        Page<Elders> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Elders> elderPage = eldersMapper.selectPage(page, wrapper);

        // 将实体对象转换为VO对象
        List<ElderListVo> voList = new ArrayList<>();
        for (Elders elder : elderPage.getRecords()) {
            ElderListVo vo = new ElderListVo();
            BeanUtils.copyProperties(elder, vo);
            voList.add(vo);
        }

        // 封装分页结果
        PageVo<ElderListVo> result = new PageVo<>();
        result.setTotal(elderPage.getTotal());
        result.setPages(elderPage.getPages());
        result.setPageNum(elderPage.getCurrent());
        result.setPageSize(elderPage.getSize());
        result.setRecords(voList);
        return result;
    }

    /**
     * 新增老人信息及联系人
     * @param dto 老人保存DTO，包含基本信息和最多两个联系人信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ElderSaveDto dto) {
        // 校验老人姓名不能为空
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException(ResultCodeEnum.ELDER_NAME_EMPTY);
        }

        // 保存老人基本信息到数据库
        Elders elder = new Elders();
        BeanUtils.copyProperties(dto, elder);
        eldersMapper.insert(elder);

        // 保存第一个联系人信息（姓名和手机号必须同时填写或同时为空）
        saveSingleContact(
                elder.getElderId(),
                dto.getFamilyContact1(),
                dto.getFamilyPhone1(),
                "联系人1"
        );

        // 保存第二个联系人信息
        saveSingleContact(
                elder.getElderId(),
                dto.getFamilyContact2(),
                dto.getFamilyPhone2(),
                "联系人2"
        );
    }

    /**
     * 保存单个联系人信息
     * 逻辑：如果姓名和手机号都为空则跳过；如果一个为空一个不为空则报错；
     * 如果都存在，则先查找或创建用户，再建立老人与用户的关联关系
     *
     * @param elderId 老人ID
     * @param contactName 联系人姓名
     * @param phone 联系人手机号
     * @param relationship 关系描述（如"联系人1"、"联系人2"）
     */
    private void saveSingleContact(Long elderId, String contactName, String phone, String relationship) {
        // 判断姓名和手机号是否为空
        boolean nameEmpty = contactName == null || contactName.trim().isEmpty();
        boolean phoneEmpty = phone == null || phone.trim().isEmpty();

        // 如果姓名和手机号都为空，直接跳过不处理
        if (nameEmpty && phoneEmpty) {
            return;
        }

        // 如果只填了其中一个，抛出参数错误异常
        if (nameEmpty || phoneEmpty) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, relationship + "姓名和手机号必须同时填写");
        }

        // 根据手机号查询用户是否已存在
        Users user = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getPhone, phone)
                        .last("limit 1")
        );

        if (user == null) {
            // 用户不存在，创建新用户
            // 不能使用全参构造器，必须使用new + set方式
            user = new Users();
            user.setRealName(contactName);
            user.setPhone(phone);
            user.setUsername(phone);  // 用户名设置为手机号
            user.setPassword("123456");  // 默认密码
            user.setUserType((byte) 2);  // 用户类型：2表示家属
            user.setStatus((byte) 1);  // 状态：1表示启用
            usersMapper.insert(user);
        } else {
            // 用户已存在，更新真实姓名
            user.setRealName(contactName);
            usersMapper.updateById(user);
        }

        // 创建老人与用户的关联关系
        Relations relation = new Relations();
        relation.setElderId(elderId);
        relation.setUserId(user.getUserId());
        relation.setRelationship(relationship);
        relationsMapper.insert(relation);
    }

    /**
     * 更新老人信息及联系人
     * @param dto 老人更新DTO，包含老人ID和需要更新的信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ElderUpdateDto dto) {
        // 查询老人是否存在
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

        // 删除该老人原有的所有联系人关系
        relationsMapper.delete(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, dto.getElderId())
        );

        // 重新保存第一个联系人
        saveSingleContact(
                dto.getElderId(),
                dto.getFamilyContact1(),
                dto.getFamilyPhone1(),
                "联系人1"
        );

        // 重新保存第二个联系人
        saveSingleContact(
                dto.getElderId(),
                dto.getFamilyContact2(),
                dto.getFamilyPhone2(),
                "联系人2"
        );
    }

    /**
     * 删除老人信息及关联的联系人关系
     * @param elderId 老人ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long elderId) {
        // 验证老人是否存在
        Elders elder = eldersMapper.selectById(elderId);
        if (elder == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_NOT_EXIST);
        }

        // 先删除该老人的所有联系人关系（外键约束）
        relationsMapper.delete(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, elderId)
        );
        // 再删除老人记录
        eldersMapper.deleteById(elderId);
    }

    /**
     * 查询老人详细信息，包括联系人信息
     * @param elderId 老人ID
     * @return 老人详情VO
     */
    @Override
    public ElderDetailVo detail(Long elderId) {
        // 查询老人基本信息
        Elders elder = eldersMapper.selectById(elderId);
        if (elder == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_NOT_EXIST);
        }

        // 将实体转换为VO
        ElderDetailVo detailVo = new ElderDetailVo();
        BeanUtils.copyProperties(elder, detailVo);

        // 查询该老人的所有联系人列表
        List<ElderRelativeVo> relativeList = relationsMapper.selectRelativeListByElderId(elderId);

        // 如果有第一个联系人，设置到VO中
        if (relativeList != null && !relativeList.isEmpty()) {
            ElderRelativeVo relative1 = relativeList.get(0);
            detailVo.setFamilyContact1(relative1.getRealName());
            detailVo.setFamilyPhone1(relative1.getPhone());
        }

        // 如果有第二个联系人，设置到VO中
        if (relativeList != null && relativeList.size() > 1) {
            ElderRelativeVo relative2 = relativeList.get(1);
            detailVo.setFamilyContact2(relative2.getRealName());
            detailVo.setFamilyPhone2(relative2.getPhone());
        }

        return detailVo;
    }
}