package com.carry.customerflow.service.imp;

import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.mapper.MachineMapper;
import com.carry.customerflow.mapper.ShopMapper;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.service.ShopService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
@Service
public class ShopServiceImp implements ShopService {
    @Resource
    private ShopMapper shopMapper;

    @Resource
    private Shop_dataMapper shop_dataMapper;

    @Override
    public List<Shop> findShopByUsername(String username) {
        return shopMapper.findShopByUsername(username) ;
    }

    @Override
    public void insertShop(String username, String longitude, String latitude, String address) {
        shopMapper.insertShop(username,longitude,latitude,address);
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);

        if (shop_dataMapper.selectWithin1hourByAddress(address)==0)
        shop_dataMapper.insertShop(address,hours);
    }

    @Override
    public void deleteShop(String address) {
        shopMapper.deleteShop(address);
    }

    @Override
    public Integer checkShop(String address) {
        return shopMapper.checkShop(address);
    }

    @Override
    public void changeBondShop(String username, String address) {
        shopMapper.changeBondShop(username,address);
    }

}
