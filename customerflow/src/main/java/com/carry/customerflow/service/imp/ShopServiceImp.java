package com.carry.customerflow.service.imp;

import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.mapper.MachineMapper;
import com.carry.customerflow.mapper.ShopMapper;
import com.carry.customerflow.service.ShopService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ShopServiceImp implements ShopService {
    @Resource
    private ShopMapper shopMapper;

    @Override
    public List<Shop> findShopByUsername(String username) {
        return shopMapper.findShopByUsername(username) ;
    }

    @Override
    public void insertShop(String username, String longitude, String latitude, String address) {
        shopMapper.insertShop(username,longitude,latitude,address);
    }
}
