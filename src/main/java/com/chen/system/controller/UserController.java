package com.chen.system.controller;

import com.chen.common.ApiResponse;
import com.chen.system.entity.User;
import com.chen.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @RequiresPermissions("user:read")
    public ApiResponse<List<User>> list() {
        return ApiResponse.ok(userService.list());
    }

    @PostMapping
    @RequiresPermissions("user:write")
    public ApiResponse<User> create(@RequestBody User user) {
        return ApiResponse.ok(userService.create(user));
    }
}
