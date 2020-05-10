package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Customer_Inshop;
import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.mapper.Customer_InshopMapper;
import com.carry.customerflow.utils.DataUtil;
import com.carry.customerflow.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class Customer_InshopController {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private Customer_InshopMapper customer_inshopMapper;

    @GetMapping("/searchAllCustomer")
    public Msg searchAllCustomer(@RequestParam("address")String address){
        try{

            List<Customer_Inshop> customer_inshopList = customer_inshopMapper.searchAllCustomer(address);

            List<Customer_Inshop> customer_inshopListRs = new ArrayList<>();

            Map<String,Object> customerMap = redisUtil.hmget("customer");

            for (Map.Entry<String, Object> subCustomerMap:customerMap.entrySet()) {
                Map<String,Object> subCustomerMap_1 = (Map)subCustomerMap.getValue();
                if (((String)subCustomerMap_1.get("address")).equals(address)&&((Integer)subCustomerMap_1.get("visited_times"))>0){
                    Timestamp last_in_time = new Timestamp((Long)subCustomerMap_1.get("last_in_time"));
                    Customer_Inshop customer_inshop = Customer_Inshop.builder()
                            .mac((String)subCustomerMap_1.get("mac"))
                            .nickname((String)subCustomerMap_1.get("nickname"))
                            .visited_times((Integer)subCustomerMap_1.get("visited_times"))
                            .last_in_time(last_in_time).build();
                    customer_inshopListRs.add(customer_inshop);
                }
            }

            Iterator<Customer_Inshop> iterator = customer_inshopList.iterator();
            while (iterator.hasNext()) {
                Customer_Inshop integer = iterator.next();
                for (Customer_Inshop customer_inshop:customer_inshopListRs)
                {
                    if (integer.getMac().equals(customer_inshop.getMac()))
                        iterator.remove();
                }
            }
            customer_inshopListRs.addAll(customer_inshopList);

            return Msg.success().setData(customer_inshopListRs);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }


    @GetMapping("/searchCustomerInshopNow")
    public Msg searchCustomerInshopNow(@RequestParam("address")String address){
        try{

            List<Customer_Inshop> customer_inshopListRs = new ArrayList<>();

            Map<String,Object> customerMap = redisUtil.hmget("customer");

            for (Map.Entry<String, Object> subCustomerMap:customerMap.entrySet()) {
                Map<String,Object> subCustomerMap_1 = (Map)subCustomerMap.getValue();
                if (((String)subCustomerMap_1.get("address")).equals(address)&&((Integer)subCustomerMap_1.get("inJudge"))==1){
                    Timestamp last_in_time = new Timestamp((Long)subCustomerMap_1.get("last_in_time"));
                    Customer_Inshop customer_inshop = Customer_Inshop.builder()
                            .mac((String)subCustomerMap_1.get("mac"))
                            .nickname((String)subCustomerMap_1.get("nickname"))
                            .visited_times((Integer)subCustomerMap_1.get("visited_times"))
                            .last_in_time(last_in_time).build();
                    customer_inshopListRs.add(customer_inshop);
                }
            }

            return Msg.success().setData(customer_inshopListRs);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    /**
     * 根据mac地址查找客人
     * @param mac
     * @return
     */
    @GetMapping("/searchCustomerByMac")
    public Msg searchCustomerByMac(@RequestParam("address")String address,@RequestParam("mac")String mac){
        try{
            Map<String,Object> customerMap = redisUtil.hmget("customer");

            for (Map.Entry<String, Object> subCustomerMap:customerMap.entrySet()) {
                Map<String,Object> subCustomerMap_1 = (Map)subCustomerMap.getValue();
                if (((String)subCustomerMap_1.get("address")).equals(address)&&((String)subCustomerMap_1.get("mac")).equals(mac)&&((Integer)subCustomerMap_1.get("visited_times"))>0){
                    Timestamp last_in_time = new Timestamp((Long)subCustomerMap_1.get("last_in_time"));
                    Customer_Inshop customer_inshop = Customer_Inshop.builder()
                            .mac((String)subCustomerMap_1.get("mac"))
                            .nickname((String)subCustomerMap_1.get("nickname"))
                            .visited_times((Integer)subCustomerMap_1.get("visited_times"))
                            .last_in_time(last_in_time).build();
                    return Msg.success().setData(customer_inshop);
                }
            }
        return Msg.success().setData(customer_inshopMapper.searchCustomerByMac(address,mac));
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    /**
     * 编辑昵称
     * @param mac
     * @param nickname
     * @return
     */
    @PostMapping("/editNickname")
    public Msg editNickname(@RequestParam("mac")String mac,@RequestParam("nickname")String nickname){
        try{

            customer_inshopMapper.editNickname(mac,nickname);

            Map<String,Object> customerMap = redisUtil.hmget("customer");

            for (Map.Entry<String, Object> subCustomerMap:customerMap.entrySet()) {
                if (mac.equals(subCustomerMap.getKey())) {
                    Map<String, Object> subCustomerMap_1 = (Map) subCustomerMap.getValue();
                    if (((String) subCustomerMap_1.get("mac")).equals(mac) && ((Integer) subCustomerMap_1.get("visited_times")) > 0) {
                        subCustomerMap_1.put("nickname",nickname);
                        dataUtil.refreshCache(mac,subCustomerMap_1);
                    }
                }
            }

//            System.out.println(redisUtil.hmget("customer"));
            return Msg.success().setMessage("编辑成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

}
