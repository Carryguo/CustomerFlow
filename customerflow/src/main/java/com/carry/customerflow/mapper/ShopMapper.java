package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopMapper {
    List<Shop> findShopByUsername(@Param("username")String username);
    void insertShop(@Param("username")String username,@Param("longitude")String longitude,@Param("latitude")String latitude,@Param("address")String address);
    List<String> findAllShop();
    void deleteShop(@Param("address")String address);
    Integer checkShop(@Param("address")String address);
    void changeBondShop(@Param("username")String username,@Param("address")String address);
    void deleteShopByBossName(@Param("username")String username);
    List<Shop> searchShopByAddress(@Param("address")String address,@Param("username") String username);
}
