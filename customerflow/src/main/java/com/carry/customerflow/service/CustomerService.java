package com.carry.customerflow.service;

import com.carry.customerflow.bean.Customer;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

public interface CustomerService {
    Customer findCustomerByMac(String mac);
    void insertCustomer(String mac, Integer rssi, String address, Timestamp first_in_time, Timestamp latest_in_time, Timestamp bean, Integer inJudge,Integer visited_times);

}
