package com.carry.customerflow.service;

import com.carry.customerflow.bean.Shop;


import java.util.List;

public interface RegisterService {
    List<Shop> findAddressByUsername( String username);
    void insertUser(String uid,String username,String password,String address,String bossname);
    Integer checkExist(String username);
}
