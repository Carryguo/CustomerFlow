package com.carry.customerflow.service;

import com.carry.customerflow.bean.User;

public interface UserService {
    User findByUsername(String username);
}
