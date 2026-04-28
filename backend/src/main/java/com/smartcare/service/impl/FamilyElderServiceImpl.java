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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteElder(Long userId, Long elderId) {
        if (userId == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "用户ID不能为空");
        }

        if (elderId == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "老人ID不能为空");
        }

        int relationCount = relationsMapper.countFamilyElderRelation(userId, elderId);
        if (relationCount <= 0) {
            throw new BusinessException(ResultCodeEnum.RELATION_NOT_EXIST, "无权删除该老人信息");
        }

        int deleteCount = relationsMapper.deleteFamilyElderRelation(userId, elderId);
        if (deleteCount <= 0) {
            throw new BusinessException(ResultCodeEnum.RELATION_DELETE_ERROR);
        }
    }

    /**
     * 查询指定家属绑定的老人列表
     */
    @Override
    public List<ElderVO> listMyElders(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "用户ID不能为空");
        }

        return eldersMapper.selectFamilyElderList(userId);
    }

    /**
     * 添加老人信息，并绑定当前家属
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addElder(Long userId, ElderAddDTO dto) {
        if (userId == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "用户ID不能为空");
        }

        if (dto == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "老人信息不能为空");
        }

        Elders elder = new Elders();
        elder.setName(dto.getName());
        elder.setAge(dto.getAge());

        if (dto.getGender() != null) {
            elder.setGender(dto.getGender().byteValue());
        }

        elder.setAddress(dto.getAddress());
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
     * 删除老人绑定关系
     *
     * 注意：
     * 这里优先删除 relations 关系，不直接删 elders 表。
     * 因为一个老人理论上可能被多个家属绑定。
     *
     * 如果你的业务是“一删就彻底删除老人”，可以在删除关系后再判断是否还有其他关系，
     * 没有的话再删 elders。
     */


        /*
         * 如果你希望删除关系后，同时删除老人主表数据，可以打开下面逻辑。
         *
         * Long remainCount = relationsMapper.countByElderId(elderId);
         * if (remainCount == null || remainCount <= 0) {
         *     int elderDeleteCount = eldersMapper.deleteById(elderId);
         *     if (elderDeleteCount <= 0) {
         *         throw new BusinessException(ResultCodeEnum.ELDER_DELETE_ERROR);
         *     }
         * }
         */


    /**
     * 查询某个老人的跌倒事件分页列表
     */
    @Override
    public PageVo<ElderFallEventVO> listFallEvents(Long userId, Long elderId, PageQueryDTO pageQuery) {
        if (userId == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR, "用户ID不能为空");
        }

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
}