package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Customer_Inshop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface Customer_InshopMapper {
    List<Customer_Inshop> searchAllCustomer(@Param("address")String address);
    Customer_Inshop searchCustomerByMac(@Param("address")String address,@Param("mac")String mac);
    Integer editNickname(@Param("mac")String mac,@Param("nickname")String nickname);
}
