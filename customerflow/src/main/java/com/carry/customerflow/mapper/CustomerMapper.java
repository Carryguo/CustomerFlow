package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Customer;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

public interface CustomerMapper {
    Customer findCustomerByMac(@Param("mac")String mac);
    void insertCustomer(@Param("mac")String mac, @Param("rssi")Integer rssi, @Param("address")String address, @Param("first_in_time")Timestamp first_in_time,@Param("latest_in_time")Timestamp latest_in_time,@Param("bean")Timestamp bean,@Param("inJudge")Integer inJudge,@Param("visited_times")Integer visited_times);
}
