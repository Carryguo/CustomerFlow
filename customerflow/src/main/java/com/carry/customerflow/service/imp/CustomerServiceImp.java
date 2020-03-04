package com.carry.customerflow.service.imp;

import com.carry.customerflow.bean.Customer;
import com.carry.customerflow.mapper.CustomerMapper;
import com.carry.customerflow.service.CustomerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;

@Service
public class CustomerServiceImp implements CustomerService {
    @Resource
    private CustomerMapper customerMapper;
    @Override
    public Customer findCustomerByMac(String mac) {
        return customerMapper.findCustomerByMac(mac);
    }

    @Override
    public void insertCustomer(String mac, Integer rssi, String address, Timestamp first_in_time, Timestamp latest_in_time, Timestamp bean, Integer inJudge,Integer visited_times) {
        customerMapper.insertCustomer(mac,rssi,address,first_in_time,latest_in_time,bean,inJudge,visited_times);
    }
}
