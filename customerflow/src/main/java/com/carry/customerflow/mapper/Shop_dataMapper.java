package com.carry.customerflow.mapper;

import org.apache.ibatis.annotations.Param;

public interface Shop_dataMapper {
    Integer selectWithin1hour();
    void insertShop (@Param("address")String address,@Param("hours")Integer hours);
    void updateWithin1hour(@Param("hours")Integer hours,@Param("num")Integer num);
    void updateShop_data(@Param("address")String address,@Param("customer")Integer customer,@Param("newIn")Integer newIn,@Param("hour_in_customer")Integer hour_in_customer,@Param("walker")Integer walker);

}
