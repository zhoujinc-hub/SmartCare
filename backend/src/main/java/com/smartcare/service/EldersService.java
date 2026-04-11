package com.smartcare.service;

import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.dto.elder.ElderSaveDto;
import com.smartcare.dto.elder.ElderUpdateDto;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderDetailVo;
import com.smartcare.vo.elder.ElderListVo;

public interface EldersService {

    PageVo<ElderListVo> list(ElderQueryDto dto);

    void add(ElderSaveDto dto);

    void update(ElderUpdateDto dto);

    void delete(Long elderId);

    ElderDetailVo detail(Long elderId);
}
