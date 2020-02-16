package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RegisterMapper {
    List<Shop> findAddressByUsername(@Param("username") String username);
    void insertUser(@Param("uid")String uid,@Param("username") String username,@Param("password")String password,@Param("address")String address);
}
