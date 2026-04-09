package com.smartcare.controller;

import com.smartcare.entity.Cameras;
import com.smartcare.service.impl.CamerasServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cameras")
public class CamerasController {

    private final CamerasServiceImpl camerasService;

    public CamerasController(CamerasServiceImpl camerasService) {
        this.camerasService = camerasService;
    }

    @GetMapping("/list")
    public List<Cameras> list() {
        return camerasService.list();
    }

    @GetMapping("/{id}")
    public Cameras getById(@PathVariable Long id) {
        return camerasService.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Cameras cameras) {
        return camerasService.save(cameras);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Cameras cameras) {
        return camerasService.updateById(cameras);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return camerasService.removeById(id);
    }
}