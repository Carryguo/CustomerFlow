package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShopController
{
    @Autowired
    private ShopService shopService;

    /**
     * 根据店主的名字返回店铺
     * @param username
     * @return
     */
    @GetMapping("/findShopByUsername")
    public Msg findShopByUsername(@RequestParam("username") String username){
        //这个地方到时候要从Session中获取用户名放进去
        try{
            List<Shop> shopList = shopService.findShopByUsername(username);
            return Msg.success(shopList).setMessage("返回店铺成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回商店失败");
        }
    }

    /**
     * 添加店铺
     * @param username
     * @param longitude
     * @param latitude
     * @param address
     * @return
     */
    @PostMapping("/insertShop")
    public Msg insertShop(@RequestParam("username")String username,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude,@RequestParam("address")String address)
    {
        //这个地方到时候要从Session中获取用户名放进去
        try{
            shopService.insertShop(username,longitude,latitude,address);
            return Msg.success().setMessage("添加店铺成功");
        }catch (DuplicateKeyException e)
        {
            return Msg.failure().setCode(401).setMessage("此处店铺已被添加,请不要重复添加");
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setMessage("添加店铺异常");
        }
    }
}
