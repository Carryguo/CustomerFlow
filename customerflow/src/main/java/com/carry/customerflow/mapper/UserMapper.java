package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findByUsernameTest(@Param("username") String username);
    List<User> searchAllBoss();
    List<User> searchStaffnameByBossname(@Param("bossname")String bossname);
    void deleteUser(@Param("username")String username);
    void deleteStaff(@Param("bossname")String bossname);
}
