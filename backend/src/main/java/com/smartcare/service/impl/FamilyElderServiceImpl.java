package com.smartcare.service.impl;

import com.smartcare.common.ResultCodeEnum;
import com.smartcare.common.exception.BusinessException;
import com.smartcare.dto.elder.ElderAddDTO;
import com.smartcare.dto.elder.PageQueryDTO;
import com.smartcare.entity.Elders;
import com.smartcare.entity.Relations;
import com.smartcare.mapper.EldersMapper;
import com.smartcare.mapper.FallEventsMapper;
import com.smartcare.mapper.RelationsMapper;
import com.smartcare.service.FamilyElderService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderFallEventVO;
import com.smartcare.vo.elder.ElderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 家属端老人业务实现类
 */
@Service
@RequiredArgsConstructor
public class FamilyElderServiceImpl implements FamilyElderService {

    private final EldersMapper eldersMapper;

    private final RelationsMapper relationsMapper;

    private final FallEventsMapper fallEventsMapper;

    /**
     * 查询当前家属绑定的老人列表
     */
    @Override
    public List<ElderVO> listMyElders() {
        Long userId = getCurrentUserId();
        return eldersMapper.selectFamilyElderList(userId);
    }

    /**
     * 添加老人信息，并绑定当前家属
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addElder(ElderAddDTO dto) {
        Long userId = getCurrentUserId();

        Elders elder = new Elders();
        elder.setName(dto.getName());
        elder.setAge(dto.getAge());

        if (dto.getGender() != null) {
            elder.setGender(dto.getGender().byteValue());
        }

        elder.setAddress(dto.getAddress());

        /*
         * 如果你的 Elders 实体和 elders 表里有 health_notes 字段，
         * 就保留这一行。
         *
         * 如果实体没有 setHealthNotes 方法，删掉这一行。
         */
        elder.setHealthNotes(dto.getHealthNotes());

        int insertCount = eldersMapper.insert(elder);
        if (insertCount <= 0 || elder.getElderId() == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_SAVE_ERROR);
        }

        Relations relation = new Relations();
        relation.setUserId(userId);
        relation.setElderId(elder.getElderId());
        relation.setRelationship("家属");

        int relationCount = relationsMapper.insert(relation);
        if (relationCount <= 0 || relation.getRelationId() == null) {
            throw new BusinessException(ResultCodeEnum.RELATION_SAVE_ERROR);
        }
    }

    /**
     * 查询某个老人的跌倒事件分页列表
     */
    @Override
    public PageVo<ElderFallEventVO> listFallEvents(Long elderId, PageQueryDTO pageQuery) {
        Long userId = getCurrentUserId();

        if (elderId == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "老人ID不能为空");
        }

        fixPageQuery(pageQuery);

        Elders elder = eldersMapper.selectById(elderId);
        if (elder == null) {
            throw new BusinessException(ResultCodeEnum.ELDER_NOT_EXIST);
        }

        int relationCount = relationsMapper.countFamilyElderRelation(userId, elderId);
        if (relationCount <= 0) {
            throw new BusinessException(ResultCodeEnum.RELATION_NOT_EXIST, "无权查看该老人信息");
        }

        Long total = fallEventsMapper.countFamilyFallEvents(userId, elderId);
        if (total == null) {
            total = 0L;
        }

        List<ElderFallEventVO> records = fallEventsMapper.selectFamilyFallEventPage(
                userId,
                elderId,
                pageQuery.getOffset(),
                pageQuery.getPageSize()
        );

        PageVo<ElderFallEventVO> pageVo = new PageVo<>();
        pageVo.setTotal(total);
        pageVo.setPageNum(pageQuery.getPageNum().longValue());
        pageVo.setPageSize(pageQuery.getPageSize().longValue());
        pageVo.setPages(calculatePages(total, pageQuery.getPageSize()));
        pageVo.setRecords(records);

        return pageVo;
    }

    /**
     * 修正分页参数
     */
    private void fixPageQuery(PageQueryDTO pageQuery) {
        if (pageQuery == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "分页参数不能为空");
        }

        if (pageQuery.getPageNum() == null || pageQuery.getPageNum() < 1) {
            pageQuery.setPageNum(1);
        }

        if (pageQuery.getPageSize() == null || pageQuery.getPageSize() < 1) {
            pageQuery.setPageSize(10);
        }

        if (pageQuery.getPageSize() > 100) {
            pageQuery.setPageSize(100);
        }
    }

    /**
     * 计算总页数
     */
    private Long calculatePages(Long total, Integer pageSize) {
        if (total == null || total <= 0) {
            return 0L;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 获取当前登录家属用户ID
     *
     * 当前先写死 1L 联调。
     * 后续接登录认证后，替换为真实登录用户ID。
     */
    private Long getCurrentUserId() {
        return 1L;
    }
}