package com.carry.customerflow.controller;

import com.carry.customerflow.bean.CustomerPerHour;
import com.carry.customerflow.bean.InCustomerPerHour;
import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Shop_data;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.utils.DataUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Shop_DataController {

    @Autowired
    private Shop_dataMapper shop_dataMapper;

    @Autowired
    private DataUtil dataUtil;

    @GetMapping("/getMainData")
    public Msg getMainData(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try{
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

//            System.out.println(dataUtil.getDayDiffer(Date.valueOf(dateTime),new Date(System.currentTimeMillis())));

            Integer walker_number = 0;
            Integer customer_number = 0;
            Integer new_customer = 0;
            Integer jumpout = 0;
            List<Shop_data> shop_dataList = shop_dataMapper.getMainData(address,dateTime);
            if (shop_dataList.size()==0)
                return Msg.failure("门店未添加或已经被删除").setCode(402);
            for (Shop_data shop_data:shop_dataList) {
                walker_number+=shop_data.getWalker_number();
                customer_number+=shop_data.getCustomer_number();
                new_customer+=shop_data.getNew_customer();
                jumpout+=shop_data.getJump_out();
            }
            Integer dynamicconsumer = shop_dataList.get(0).getDynamic_customer();
            Map<String,Integer> map=new HashMap<>();
            map.put("walkerNumber",walker_number);
            map.put("consumerNumber",customer_number);
            map.put("newConsumer",new_customer);
            map.put("jmpOut",jumpout);
            map.put("dynamicConsumer",dynamicconsumer);


            return Msg.success().setData(map);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }


    @GetMapping("/getCustomerPerHour")
    public Msg getCustomerPerHour(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try {
            List<CustomerPerHour> customerPerHourList = shop_dataMapper.getCustomerPerHour(address, dateTime);
            Collections.reverse(customerPerHourList);
            return Msg.success().setData(customerPerHourList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    @GetMapping("/getInCustomerPerHour")
    public Msg getInCustomerPerHour(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try {
            List<InCustomerPerHour> inCustomerPerHourList = shop_dataMapper.getInCustomerPerHour(address, dateTime);
            Collections.reverse(inCustomerPerHourList);
            return Msg.success().setData(inCustomerPerHourList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

}
