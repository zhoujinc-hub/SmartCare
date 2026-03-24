package com.smartcare.controller;

import com.smartcare.entity.AlertLogs;
import com.smartcare.service.impl.AlertLogsServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertLogs")
public class AlertLogsController {

    private final AlertLogsServiceImpl alertLogsService;

    public AlertLogsController(AlertLogsServiceImpl alertLogsService) {
        this.alertLogsService = alertLogsService;
    }

    @GetMapping("/list")
    public List<AlertLogs> list() {
        return alertLogsService.list();
    }

    @GetMapping("/{id}")
    public AlertLogs getById(@PathVariable Long id) {
        return alertLogsService.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody AlertLogs alertLogs) {
        return alertLogsService.save(alertLogs);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody AlertLogs alertLogs) {
        return alertLogsService.updateById(alertLogs);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return alertLogsService.removeById(id);
    }
}