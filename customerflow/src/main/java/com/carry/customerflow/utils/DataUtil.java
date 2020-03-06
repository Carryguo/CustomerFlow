package com.carry.customerflow.utils;

import com.carry.customerflow.bean.Customer;
import com.carry.customerflow.mapper.ShopMapper;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataUtil {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Shop_dataMapper shop_dataMapper;

    @Autowired
    private ShopMapper shopMapper;

    private Map<String,Object> customerMap;
    private Map<String,Object> subCcustomerMap;

    private Timestamp timestamp;


    /**
     * 检查Mac是否已经存在过
     * @param mac
     * @return
     */
    public  boolean checkExist(String mac){
        if (redisUtil.hget("customer",mac)!=null)
            return true;
        else{
            Customer customer = customerService.findCustomerByMac(mac);
            if (customer==null)
                return false;
            cacheCustomer(customer.getMac(),customer.getRssi(),customer.getAddress(),customer.getFirst_in_time(),customer.getLatest_in_time(),customer.getBeat(),customer.getInJudge(),customer.getVisited_times());
            return true;
        }
    }

    private void cacheCustomer(String mac, Integer rssi, String address, Timestamp first_in_time,Timestamp latest_in_time,Timestamp beat,Integer inJudge,Integer visited_times){
        customerMap = new HashMap<>();
        subCcustomerMap = new HashMap<>();
        subCcustomerMap.put("mac",mac);
        subCcustomerMap.put("rssi",rssi);
        subCcustomerMap.put("address",address);
        subCcustomerMap.put("first_in_time",first_in_time);
        subCcustomerMap.put("latest_in_time",latest_in_time);
        subCcustomerMap.put("beat",beat);
        subCcustomerMap.put("inJudge",inJudge);
        subCcustomerMap.put("visited_times",visited_times);
        customerMap.put(mac,subCcustomerMap);
        redisUtil.hmset("customer",customerMap);
    }

    public boolean insertCustomer(String mac,Integer rssi,String address,Integer inJudge,Integer visited_times){
        timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println("insertCustomer的timestamp:"+timestamp);
//        System.out.println(timestamp);
        //插入数据库
        customerService.insertCustomer(mac,rssi,address,timestamp,timestamp,timestamp,inJudge,visited_times);
//        System.out.println("插入了数据");
        //插入redis缓存
        cacheCustomer(mac,rssi,address,timestamp,timestamp,timestamp,inJudge,visited_times);
        return true;
    };

    public boolean initShop_data(){
        if (shop_dataMapper.selectWithin1hour()==0){
            insertShop();
            return true;
        }else{
            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR_OF_DAY);
            List<String> addressList = shopMapper.findAllShop();
            shop_dataMapper.updateWithin1hour(hours,addressList.size());
        }
        return true;
    }

    public boolean insertShop(){
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        List<String> addressList = shopMapper.findAllShop();
        for (String address:addressList) {
            shop_dataMapper.insertShop(address,hours);
        }
        return true;
    }

    //获取相应mac的用户信息
    public Map<String,Object> getCustomerMap(String mac)
    {
        if (redisUtil.hget("customer",mac)!=null)
            return (Map)redisUtil.hget("customer",mac);
        else{
            Customer customer = customerService.findCustomerByMac(mac);
            cacheCustomer(customer.getMac(),customer.getRssi(),customer.getAddress(),customer.getFirst_in_time(),customer.getLatest_in_time(),customer.getBeat(),customer.getInJudge(),customer.getVisited_times());
            return (Map)redisUtil.hget("customer",mac);
        }
    }

    //更新缓存信息
    public void refreshCache(String mac,Map customerMap){
        redisUtil.hset("customer",mac,customerMap);
    }
}

