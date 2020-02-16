package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User findByUsername(@Param("username") String username);
}
