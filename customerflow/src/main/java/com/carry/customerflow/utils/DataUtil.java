package com.carry.customerflow.utils;

import com.carry.customerflow.bean.Customer;
import com.carry.customerflow.bean.Machine;
import com.carry.customerflow.mapper.ShopMapper;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.service.CustomerService;
import com.carry.customerflow.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private MachineService machineService;

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
            cacheCustomer(customer.getMac(),customer.getRssi(),customer.getAddress(),customer.getFirst_in_time(),customer.getLatest_in_time(),customer.getBeat(),customer.getInJudge(),customer.getVisited_times(),customer.getLast_in_time(),customer.getNickname());
            return true;
        }
    }

    private void cacheCustomer(String mac, Integer rssi, String address, Timestamp first_in_time,Timestamp latest_in_time,Timestamp beat,Integer inJudge,Integer visited_times,Timestamp last_in_time,String nickname){
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
        subCcustomerMap.put("last_in_time",last_in_time);
        subCcustomerMap.put("nickname",nickname);
        customerMap.put(mac,subCcustomerMap);
        redisUtil.hmset("customer",customerMap);
    }

    public boolean insertCustomer(String mac,Integer rssi,String address,Integer inJudge,Integer visited_times){
        timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println("insertCustomer的timestamp:"+timestamp);
//        System.out.println(timestamp);
        //插入数据库
        customerService.insertCustomer(mac,rssi,address,timestamp,timestamp,timestamp,inJudge,visited_times,timestamp);
//        System.out.println("插入了数据");
        //插入redis缓存
        cacheCustomer(mac,rssi,address,timestamp,timestamp,timestamp,inJudge,visited_times,timestamp,null);
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
            cacheCustomer(customer.getMac(),customer.getRssi(),customer.getAddress(),customer.getFirst_in_time(),customer.getLatest_in_time(),customer.getBeat(),customer.getInJudge(),customer.getVisited_times(),customer.getLast_in_time(),customer.getNickname());
            return (Map)redisUtil.hget("customer",mac);
        }
    }

    //更新缓存信息
    public void refreshCache(String mac,Map customerMap){
        redisUtil.hset("customer",mac,customerMap);
    }

    //设备缓存初始化
    public void refreshMachineCache(){
        List<Machine> machineList = machineService.findAllMachine();
         Map<String,Object> machineMap = new HashMap<>();
        for (Machine machine:machineList) {
            Map<String,Object> subMachineMap = new HashMap<>();
            subMachineMap.put("machineId",machine.getMachineId());
            subMachineMap.put("address",machine.getAddress());
            subMachineMap.put("status",machine.getStatus());
            subMachineMap.put("rssi",machine.getRssi());
            subMachineMap.put("leastRssi",machine.getLeastRssi());
            subMachineMap.put("beat",machine.getBeat());
            machineMap.put(machine.getMachineId(),subMachineMap);
        }
        redisUtil.del("machine");
        redisUtil.hmset("machine",machineMap);
    }

    public void refreshMachineCacheBeat(String machineId){
        Map<String,Object> subMachineMap = (Map)redisUtil.hget("machine",machineId);
        subMachineMap.put("beat",new Timestamp(System.currentTimeMillis()));
        redisUtil.hset("machine",machineId,subMachineMap);
    }

    public int getDayDiffer(Date startDate, Date endDate) throws ParseException {
        //判断是否跨年
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String startYear = yearFormat.format(startDate);
        String endYear = yearFormat.format(endDate);
        if (startYear.equals(endYear)) {
            /*  使用Calendar跨年的情况会出现问题    */
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int startDay = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTime(endDate);
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);
            return endDay - startDay;
        } else {
            /*  跨年不会出现问题，需要注意不满24小时情况（2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 0）  */
            //  只格式化日期，消除不满24小时影响
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            long startDateTime = dateFormat.parse(dateFormat.format(startDate)).getTime();
            long endDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
            return (int) ((endDateTime - startDateTime) / (1000 * 3600 * 24));
        }
    }

}




