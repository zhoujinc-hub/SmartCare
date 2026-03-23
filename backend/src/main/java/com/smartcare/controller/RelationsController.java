package com.smartcare.controller;

import com.smartcare.entity.Relations;
import com.smartcare.service.impl.RelationsServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relations")
public class RelationsController {

    private final RelationsServiceImpl relationsService;

    public RelationsController(RelationsServiceImpl relationsService) {
        this.relationsService = relationsService;
    }

    @GetMapping("/list")
    public List<Relations> list() {
        return relationsService.list();
    }

    @GetMapping("/{id}")
    public Relations getById(@PathVariable Long id) {
        return relationsService.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Relations relations) {
        return relationsService.save(relations);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Relations relations) {
        return relationsService.updateById(relations);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return relationsService.removeById(id);
    }
}