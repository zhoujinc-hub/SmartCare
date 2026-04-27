package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.elder.ElderAddDTO;
import com.smartcare.dto.elder.PageQueryDTO;
import com.smartcare.service.FamilyElderService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderFallEventVO;
import com.smartcare.vo.elder.ElderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家属端老人管理接口
 */
@RestController
@RequestMapping("/family/elder")
@RequiredArgsConstructor
public class FamilyElderController {

    private final FamilyElderService familyElderService;

    /**
     * 查询当前家属绑定的老人列表
     *
     * 前端接口：
     * GET /family/elder/list
     */
    @GetMapping("/list")
    public Result<List<ElderVO>> listMyElders() {
        return Result.ok(familyElderService.listMyElders());
    }

    /**
     * 添加老人信息，并绑定当前家属
     *
     * 前端接口：
     * POST /family/elder/add
     */
    @PostMapping("/add")
    public Result<Void> addElder(@Valid @RequestBody ElderAddDTO dto) {
        familyElderService.addElder(dto);
        return Result.ok();
    }

    /**
     * 查询某个老人的跌倒事件记录
     *
     * 前端接口：
     * GET /family/elder/{elderId}/fallEvents?pageNum=1&pageSize=100
     */
    @GetMapping("/{elderId}/fallEvents")
    public Result<PageVo<ElderFallEventVO>> listFallEvents(@PathVariable Long elderId,
                                                           PageQueryDTO pageQuery) {
        return Result.ok(familyElderService.listFallEvents(elderId, pageQuery));
    }
}