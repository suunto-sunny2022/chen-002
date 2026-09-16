package com.chen.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.system.entity.User;
import com.chen.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
            .eq(User::getUsername, username)
            .last("LIMIT 1"));
    }

    public List<User> list() {
        return userMapper.selectList(null);
    }

    @Transactional
    public User create(User user) {
        user.setId(null);
        userMapper.insert(user);
        return user;
    }
}
