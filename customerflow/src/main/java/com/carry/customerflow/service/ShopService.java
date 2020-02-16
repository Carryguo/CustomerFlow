package com.carry.customerflow.service;

import com.carry.customerflow.bean.Shop;

import java.util.List;

public interface ShopService {
    List<Shop> findShopByUsername(String username);
    void insertShop(String username,String longitude,String latitude,String address);
}
