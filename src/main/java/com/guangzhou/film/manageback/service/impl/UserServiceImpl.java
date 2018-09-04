package com.guangzhou.film.manageback.service.impl;

import com.guangzhou.film.manageback.mapper.UserMapper;
import com.guangzhou.film.manageback.model.User;
import com.guangzhou.film.manageback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public void save(User user) {
        userMapper.insert(user);
    }
}
