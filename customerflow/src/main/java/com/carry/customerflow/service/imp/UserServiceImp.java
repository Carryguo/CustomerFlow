package com.carry.customerflow.service.imp;

import com.carry.customerflow.bean.User;
import com.carry.customerflow.mapper.UserMapper;
import com.carry.customerflow.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImp implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByUsernameWithoutPermission(String username) {
        return userMapper.findByUsernameWithoutPermission(username);
    }


}
