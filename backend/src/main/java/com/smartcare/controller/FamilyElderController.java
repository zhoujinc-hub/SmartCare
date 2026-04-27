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
@RequestMapping("/api/family/elder")
@RequiredArgsConstructor
public class FamilyElderController {

    private final FamilyElderService familyElderService;

    /**
     * 查询指定家属绑定的老人列表
     *
     * GET /api/family/elder/list/7
     */
    @GetMapping("/list/{userId}")
    public Result<List<ElderVO>> listMyElders(@PathVariable Long userId) {
        return Result.ok(familyElderService.listMyElders(userId));
    }

    /**
     * 添加老人信息，并绑定指定家属
     *
     * POST /api/family/elder/add/7
     */
    @PostMapping("/add/{userId}")
    public Result<Void> addElder(@PathVariable Long userId,
                                 @Valid @RequestBody ElderAddDTO dto) {
        familyElderService.addElder(userId, dto);
        return Result.ok();
    }

    /**
     * 查询某个老人的跌倒事件记录
     *
     * GET /api/family/elder/7/{elderId}/fallEvents?pageNum=1&pageSize=100
     */
    @GetMapping("/{userId}/{elderId}/fallEvents")
    public Result<PageVo<ElderFallEventVO>> listFallEvents(@PathVariable Long userId,
                                                           @PathVariable Long elderId,
                                                           PageQueryDTO pageQuery) {
        return Result.ok(familyElderService.listFallEvents(userId, elderId, pageQuery));
    }
}