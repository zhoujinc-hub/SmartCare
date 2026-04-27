package com.smartcare.service;

import com.smartcare.dto.elder.ElderAddDTO;
import com.smartcare.dto.elder.PageQueryDTO;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderFallEventVO;
import com.smartcare.vo.elder.ElderVO;

import java.util.List;

/**
 * 家属端老人业务接口
 */
public interface FamilyElderService {

    /**
     * 查询当前家属绑定的老人列表
     */
    List<ElderVO> listMyElders();

    /**
     * 添加老人信息，并绑定当前家属
     */
    void addElder(ElderAddDTO dto);

    /**
     * 查询某个老人的跌倒事件分页列表
     */
    PageVo<ElderFallEventVO> listFallEvents(Long elderId, PageQueryDTO pageQuery);
}