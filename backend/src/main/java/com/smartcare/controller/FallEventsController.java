package com.smartcare.controller;

import com.smartcare.entity.FallEvents;
import com.smartcare.service.impl.FallEventsServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fallEvents")
public class FallEventsController {

    private final FallEventsServiceImpl fallEventsService;

    public FallEventsController(FallEventsServiceImpl fallEventsService) {
        this.fallEventsService = fallEventsService;
    }

    @GetMapping("/list")
    public List<FallEvents> list() {
        return fallEventsService.list();
    }

    @GetMapping("/{id}")
    public FallEvents getById(@PathVariable Long id) {
        return fallEventsService.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody FallEvents fallEvents) {
        return fallEventsService.save(fallEvents);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody FallEvents fallEvents) {
        return fallEventsService.updateById(fallEvents);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return fallEventsService.removeById(id);
    }
}