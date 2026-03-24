package com.smartcare.controller;

import com.smartcare.entity.Users;
import com.smartcare.service.impl.UsersServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersServiceImpl usersService;

    public UsersController(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/list")
    public List<Users> list() {
        return usersService.list();
    }

    @GetMapping("/{id}")
    public Users getById(@PathVariable Long id) {
        return usersService.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Users users) {
        return usersService.save(users);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Users users) {
        return usersService.updateById(users);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return usersService.removeById(id);
    }
}