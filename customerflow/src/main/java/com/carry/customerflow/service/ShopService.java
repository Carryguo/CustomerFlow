package com.carry.customerflow.service;

import com.carry.customerflow.bean.Shop;

import java.util.List;

public interface ShopService {
    List<Shop> findShopByUsername(String username);
    void insertShop(String username,String longitude,String latitude,String address);
    void deleteShop(String address);
    Integer checkShop(String address);
    void changeBondShop(String username,String address);
    List<Shop> searchShopByAddress(String address);
}
