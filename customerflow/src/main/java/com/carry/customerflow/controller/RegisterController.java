package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    /**
     * 根据店主名查找店名让店员选择
     * @param username
     * @return
     */
    @GetMapping("/findShopByBossName")
    public Msg findShopByBossName(@RequestParam("username") String username){
        try{
        List<Shop>  shopList = registerService.findAddressByUsername(username);
        if (shopList.size()==0){
            return Msg.failure("该店主还没有添加任何店铺,或者该店主还没有注册");
        }
        List<String> shopAddressList = new ArrayList<>();
        for (Shop shop:shopList) {
            shopAddressList.add(shop.getAddress());
        }
        Map<String,List<String>> shopMap = new HashMap<>();
        shopMap.put("address",shopAddressList);
            return Msg.success(shopMap);
    }catch (Exception e){
            e.printStackTrace();
            return Msg.failure("查询店铺失败");
        }
    }

    /**
     * 注册
     * @param uid
     * @param username
     * @param password
     * @param address
     * @return
     */
    @PostMapping("/register")
    public Msg register(@RequestParam("uid")String uid,@RequestParam("username")String username,@RequestParam("password")String password,@RequestParam("address")String address){
        if ("3".equals(uid)&&"".equals(address)){
            return Msg.failure().setCode(401).setMessage("注册店员身份要查询店主的店铺,并且选择店铺进行绑定");
        }try{
        registerService.insertUser(uid,username,password,address);
        }catch (DuplicateKeyException e){
           return Msg.failure().setCode(402).setMessage("你所注册的用户名已经存在");
        }
        return Msg.success("插入用户成功");
    }
}

