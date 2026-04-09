package com.smartcare.controller;

import com.smartcare.entity.Elders;
import com.smartcare.service.impl.EldersServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elders")
public class EldersController {

    private final EldersServiceImpl eldersService;

    public EldersController(EldersServiceImpl eldersService) {
        this.eldersService = eldersService;
    }

    @GetMapping("/list")
    public List<Elders> list() {
        return eldersService.list();
    }

    @GetMapping("/{id}")
    public Elders getById(@PathVariable Long id) {
        return eldersService.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Elders elders) {
        return eldersService.save(elders);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Elders elders) {
        return eldersService.updateById(elders);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return eldersService.removeById(id);
    }
}