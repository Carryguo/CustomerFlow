package com.carry.customerflow.mapper;

import org.apache.ibatis.annotations.Param;

public interface Shop_dataMapper {
    Integer selectWithin1hour();
    void insertShop (@Param("address")String address,@Param("hours")Integer hours);
    void updateWithin1hour(@Param("hours")Integer hours,@Param("num")Integer num);
    void updateShop_data(@Param("address")String address,@Param("customer")Integer customer,@Param("newIn")Integer newIn,@Param("hour_in_customer")Integer hour_in_customer,@Param("walker")Integer walker);
    //查询当前小时进店量
    Integer searchNowHour_in_customer_number(@Param("address")String address);
    //存储跳出量、动态当前客流量和小时客流量
    void updateDataThread(@Param("address") String address,@Param("dynamic_customer")Integer dynamic_customer,@Param("jumpOut_customer")Integer jumpOut_customer,@Param("subHour_customer")Integer subHour_customer);
}
