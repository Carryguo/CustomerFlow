package com.carry.customerflow.service.imp;

import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.mapper.RegisterMapper;
import com.carry.customerflow.service.RegisterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class RegisterServiceImp implements RegisterService {
    @Resource
    private RegisterMapper registerMapper;

    @Override
    public List<Shop> findAddressByUsername(String username) {
        return registerMapper.findAddressByUsername(username);
    }

    @Override
    public void insertUser(String uid,String username, String password, String address,String bossname) {
        registerMapper.insertUser(uid,username,password,address,bossname);
    }

    @Override
    public Integer checkExist(String username) {
        return registerMapper.checkExist(username);
    }
}
