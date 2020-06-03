package com.carry.customerflow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.carry.customerflow.bean.Machine;
import com.carry.customerflow.mapper.CustomerMapper;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.service.MachineService;
import com.carry.customerflow.utils.DataUtil;
import com.carry.customerflow.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class DataProcessor implements CommandLineRunner {
    //监听器端口
    private final static int PORT = 8082;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private byte[] buf = null;
    private String strReceive;

    //解析json数据
    private JSONObject jsonObject = null;
    private JSONObject jsonObjectData = null;
    private JSONArray jsonArray = null;

    //machineMap
//    private Map<String,Object> machineMap;
//    private Map<String,Object> subMachineMap;

    //正则表达式筛选数据
    private String macM = "([a-f0-9A-F]{2}:){5}[a-f0-9A-F]{2}";
    private String rssiM = "-[1-9]\\d*";
    private String idM = "^[1-9]\\d*$";

    //数据处理
    private String machineId;
    private String mac;
    private Integer rssi;
    private String data;
    private Integer dataSize;
    private Map<String,Object> customerMap;
    private Integer timeCount;//计算离开时间
        private Timestamp latest_time;//当前时间戳
    private Integer visited_times;//访问次数

    private Integer rssiJ;
    private String address;

    //新客人
    private Integer newIn = 0;
    //人流量
    private Integer walker = 0;
    //小时客流量
//    private Integer hour_customer = 0;
    //小时进店量
    private Integer hour_in_customer = 0;
    //客流量
    private Integer customer=0;
    //跳出量
    private Integer jumpOut = 0;






    //redis工具类
    @Autowired
    private RedisUtil redisUtil;
    //service
    @Autowired
    private MachineService machineService;

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private Shop_dataMapper shop_dataMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public void run(String... args) throws Exception {
        try {
            //监听器
            ds = new DatagramSocket(PORT);
            System.out.println("等待链接");
            buf = new byte[1024];
            dp = new DatagramPacket(buf, buf.length);
            //系统启动时把machineId导入到redis缓存
//            List<Machine> machineList = machineService.findAllMachine();
//                machineMap = new HashMap<>();
//            for (Machine machine:machineList) {
//                subMachineMap = new HashMap<>();
//                subMachineMap.put("machineId",machine.getMachineId());
//                subMachineMap.put("address",machine.getAddress());
//                subMachineMap.put("status",machine.getStatus());
//                subMachineMap.put("rssi",machine.getRssi());
//                machineMap.put(machine.getMachineId(),subMachineMap);
//            }
//            redisUtil.del("machine");
//            redisUtil.hmset("machine",machineMap);
            dataUtil.refreshMachineCache();

            //初始化shop_data
            dataUtil.initShop_data();

            while (true) {
                synchronized (this) {
                    try {
                        //获取wifi探针数据
                        ds.receive(dp);
                        strReceive = new String(dp.getData(), 0, dp.getLength());
//                        System.out.println(strReceive);
                        jsonObject = JSONObject.parseObject(strReceive);

                        if (jsonObject!=null){
//                            Integer rssiJ;
//                            String address;
                            //把数据初步分析出来
                            machineId = jsonObject.getString("Id").toString();
                            data = jsonObject.getString("Data");
                            if (data != null) {
                                jsonArray = JSONArray.parseArray(data);

                            } else
                                continue;
                            //如果设备没有被添加,则不接受处理
//                            System.out.println(redisUtil.hmget("machine"));
                            if (machineId.matches(idM)&&redisUtil.hget("machine",machineId)!=null){
                                //更新设备beat;
                                dataUtil.refreshMachineCacheBeat(machineId);
//                                System.out.println("进来进行处理");
                            //分析各个mac的数据
                                dataSize = jsonArray.size();
                                for (int i = 0; i < dataSize; i++){
                                    jsonObjectData = jsonArray.getJSONObject(i);
                                    mac = jsonObjectData.getString("mac");
                                    rssi = jsonObjectData.getInteger("rssi");
//                                    System.out.println("rssi的值:"+rssi);
                                    //筛选
                                    if (mac != null && mac.matches(macM) && rssi != null && rssi.toString().matches(rssiM)&&!mac.startsWith("00:00")&&rssi>(Integer)((Map)redisUtil.hget("machine",machineId)).get("leastRssi")){
//                                        System.out.println("进来了");
                                        //当前时间戳
                                        latest_time = new Timestamp(System.currentTimeMillis());
//                                        System.out.println("DataProcessor的latest_time:"+latest_time);
                                        //获取缓存中的rssi和 address
                                        rssiJ = (Integer)((Map)redisUtil.hget("machine",machineId)).get("rssi");
                                        address = (String)((Map)redisUtil.hget("machine",machineId)).get("address");
                                        //先判断是否存在
                                        //不存在的情况
                                        if (!dataUtil.checkExist(mac)){
                                            //新客人
                                            if (rssi>=rssiJ){
                                                //加入新客人
                                                dataUtil.insertCustomer(mac,rssi,address,1,1);
                                                //加入缓存
                                                newIn ++;
                                                walker ++;
                                                customer ++;
//                                                hour_customer ++;
                                                hour_in_customer ++;
                                            }else {
                                                //加入新人流量
                                                dataUtil.insertCustomer(mac,rssi,address,0,0);
                                                walker ++;
                                            }
                                            //存在的情况
                                        }else{
                                            //获取相应mac的用户信息
                                            customerMap = dataUtil.getCustomerMap(mac);
                                            timeCount = new Long((latest_time.getTime() - (Long)customerMap.get("beat")) / (60*1000)).intValue();
//                                            System.out.println( new Timestamp((Long)customerMap.get("beat")));
//                                            System.out.println(timeCount);
                                            //这里测试是测试大于2分钟时算再进的客人,到时候再改
                                            //算再进的
                                            if (timeCount >= 15) {
                                                if (rssi>=rssiJ){
                                                    //更新cache信息
                                                    //上次进店时间为上一次的first_in_time
                                                    customerMap.put("last_in_time",(Long)customerMap.get("first_in_time"));
                                                    customerMap.put("first_in_time",latest_time);
                                                    customerMap.put("latest_in_time",latest_time);
                                                    customerMap.put("beat",latest_time);
                                                    customerMap.put("inJudge",1);
                                                    customerMap.put("rssi",rssi);
                                                    customerMap.put("visited_times",(Integer)customerMap.get("visited_times")+1);




                                                    dataUtil.refreshCache(mac,customerMap);

                                                    customer ++;
                                                    walker ++;
//                                                    hour_customer ++;
                                                    hour_in_customer ++;
                                                }else{
                                                    customerMap.put("beat",latest_time);
                                                    customerMap.put("rssi",rssi);
                                                    dataUtil.refreshCache(mac,customerMap);
                                                    walker ++;
                                                }
                                            }
                                            //出现间隔小于15分钟
                                            else{
                                                if (rssi>=rssiJ){
                                                    //还没有访问过的
                                                    if ((Integer)customerMap.get("inJudge")==0){
                                                        customer ++;
//                                                        hour_customer ++;
                                                        hour_in_customer ++;
                                                        //如果还没有进过店的,视为新客人
                                                        if ((Integer)customerMap.get("visited_times")==0)
                                                        {
                                                            newIn ++;
                                                            customerMap.put("last_in_time",latest_time);
                                                        }
                                                        //上次进店时间为上一次的first_in_time
                                                        customerMap.put("last_in_time",(Long)customerMap.get("first_in_time"));
                                                        customerMap.put("first_in_time",latest_time);
                                                        customerMap.put("visited_times",(Integer)customerMap.get("visited_times")+1);
                                                    }
                                                    customerMap.put("inJudge",1);
                                                    customerMap.put("latest_in_time",latest_time);
                                                    customerMap.put("beat",latest_time);
                                                    customerMap.put("rssi",rssi);
                                                    dataUtil.refreshCache(mac,customerMap);
                                                }else{
                                                    //客人已出去
                                                    if ((Integer)customerMap.get("inJudge")==1)
                                                    {
                                                        Long stayTime = ((Long)customerMap.get("latest_in_time")-(Long)customerMap.get("first_in_time"))/1000;
                                                        if (stayTime<50)
                                                        {
                                                            jumpOut++;
                                                        }
                                                        customerMap.put("inJudge",0);
                                                    }
                                                    customerMap.put("beat",latest_time);
                                                    customerMap.put("rssi",rssi);
                                                    dataUtil.refreshCache(mac,customerMap);
                                                }
                                            }
                                        }
                                    }else
                                        continue;
                                }
//                                System.out.println("address:"+address);
//                                System.out.println("customer:"+customer);
//                                System.out.println("newIn:"+newIn);
//                                System.out.println("jumpOut:"+jumpOut);
//                                System.out.println("walker:"+walker);
//                                System.out.println("hour_in_customer:"+hour_in_customer);
//                                System.out.println("进来");
//                                System.out.println(redisUtil.hmget("customer"));
                                //这里更新
                                if (customer!=0||newIn!=0||hour_in_customer!=0||walker!=0||jumpOut!=0){
                                    shop_dataMapper.updateShop_data(address,customer,newIn,hour_in_customer,walker,jumpOut);
                                }
                                customer = 0;
                                newIn = 0;
                                hour_in_customer = 0;
                                walker = 0;
                                jumpOut = 0;
                            }else
                                continue;
                        }
                        //try
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ds.close();
            System.out.println("退出while循环.................");
        }
        System.out.println("退出while循环.................");
    }

//        @Transactional
//        @Async
//        @Scheduled(cron = "0/5 * * * * ?")
//    public void test(){
//
//            latest_time = new Timestamp(System.currentTimeMillis());
//            System.out.println(latest_time);
//        customerMapper.insertCustomer("68:c6:3a:85:3d:8h",-20,"肇庆店",latest_time,latest_time,latest_time,1,1);
//        }



    //此进程用于存储跳出量、动态当前客流量和小时客流量
    @Transactional
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void dataThread(){
//        System.out.println("5秒一次");
        Integer subHour_customer = 0;
        //现存客流量
        Integer dynamic_customer = 0;
        //跳出量
        Integer jumpOut_customer = 0;

        String subAddress = null;

        Map<String,Object> countExtraMap = new HashMap<>();
        Map<String,Integer> subCountExtraMap;
        Map<String,Object> subCustomerMap = redisUtil.hmget("customer");
//        System.out.println(subCustomerMap);
        latest_time = new Timestamp(System.currentTimeMillis());
        for (Map.Entry<String, Object> subCustomerMap_1:subCustomerMap.entrySet()) {
               String subMac = subCustomerMap_1.getKey();
               Map<String,Object> subCustomerMap_2 = dataUtil.getCustomerMap(subMac);
               //时间间隔
               Integer countTime = new Long((latest_time.getTime() - (Long)subCustomerMap_2.get("beat")) / (60*1000)).intValue();
                //大于15分钟没有心跳的店内客人
                if (countTime>=15&&(Integer)subCustomerMap_2.get("inJudge")==1)
                {
                    Long stayTime = ((Long)subCustomerMap_2.get("latest_in_time")-(Long)subCustomerMap_2.get("first_in_time"))/1000;
                    if (stayTime<50)
                    {
                        jumpOut_customer=1;
                    }
                    subCustomerMap_2.put("inJudge",0);
                }else if (countTime<15&&(Integer)subCustomerMap_2.get("inJudge")==1){
                    dynamic_customer =1;
//                    System.out.println("进来了");
                }

            subAddress = (String)subCustomerMap_2.get("address");
                if (jumpOut_customer!=0||dynamic_customer!=0)
                if (countExtraMap.get(subAddress)==null){
                    subCountExtraMap = new HashMap<>();
                    subCountExtraMap.put("jumpOut_customer",jumpOut_customer);
                    subCountExtraMap.put("dynamic_customer",dynamic_customer);
                    countExtraMap.put(subAddress,subCountExtraMap);
                }else{
                    subCountExtraMap = (Map)countExtraMap.get(subAddress);
                    subCountExtraMap.put("jumpOut_customer",subCountExtraMap.get("jumpOut_customer")+jumpOut_customer);
                    subCountExtraMap.put("dynamic_customer",subCountExtraMap.get("dynamic_customer")+dynamic_customer);
                    countExtraMap.put(subAddress,subCountExtraMap);
                }
            jumpOut_customer = 0;
            dynamic_customer = 0;
            //存储在数据库中
            dataUtil.refreshCache(subMac,subCustomerMap_2);


        }

            //存库
        for (Map.Entry<String, Object> subCustomerMap_1:countExtraMap.entrySet()) {
            subAddress = subCustomerMap_1.getKey();
            subCountExtraMap = (Map)subCustomerMap_1.getValue();
            //查询当前小时进店量
            subHour_customer = shop_dataMapper.searchNowHour_in_customer_number(subAddress);
            //如果当前店面人流量大于小时进店量, 则小时客流量等于当前店面人流量
            if (subCountExtraMap.get("dynamic_customer")>subHour_customer)
                subHour_customer = subCountExtraMap.get("dynamic_customer");
            if (subHour_customer!=0||subCountExtraMap.get("dynamic_customer")!=0||subCountExtraMap.get("jumpOut_customer")!=0)
                shop_dataMapper.updateDataThread(subAddress,subCountExtraMap.get("dynamic_customer"),subCountExtraMap.get("jumpOut_customer"),subHour_customer);
        }

       //遍历更新设备状态
        Map<String,Object> machineMap = redisUtil.hmget("machine");
        for (Map.Entry<String, Object> subMachineMap:machineMap.entrySet()) {
            String subMachineId = subMachineMap.getKey();
            Map<String,Object> subMachineMap_1 = (Map)subMachineMap.getValue();
            Integer machineCountTime = new Long((latest_time.getTime() - (Long)subMachineMap_1.get("beat")) / (60*1000)).intValue();
            if (machineCountTime>15)
                subMachineMap_1.put("status","离线");
            else
                subMachineMap_1.put("status","在线");
            redisUtil.hset("machine",subMachineId,subMachineMap_1);
        }
    }

    //此进程用于存储用户信息和补充跳出量
    @Transactional
    @Async
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void saveDataThread(){

        Map<String,Object> subCustomerMap = redisUtil.hmget("customer");

        for (Map.Entry<String, Object> subCustomerMap_1:subCustomerMap.entrySet()) {
            String subMac = subCustomerMap_1.getKey();
            Map<String,Object> subCustomerMap_2 = dataUtil.getCustomerMap(subMac);
            Timestamp first_in_time = new Timestamp((Long)subCustomerMap_2.get("first_in_time"));
            Timestamp latest_in_time =  new Timestamp((Long)subCustomerMap_2.get("latest_in_time"));
            Timestamp last_in_time =  new Timestamp((Long)subCustomerMap_2.get("last_in_time"));
            Timestamp beat = new Timestamp((Long)subCustomerMap_2.get("beat"));
            customerMapper.updateCustomer(subMac,(Integer)subCustomerMap_2.get("rssi"),first_in_time,latest_in_time,beat,(Integer)subCustomerMap_2.get("inJudge"),(Integer)subCustomerMap_2.get("visited_times"),last_in_time);
        }
        //删除缓存
        redisUtil.del("customer");

        //添加下一个小时的店铺时间
        dataUtil.insertShop();

        //补充遗漏的跳出量
        List<String> extraJumpOutAddressList = customerMapper.searchExtraJumpOut();
        customerMapper.updateInjudge();
        for (String extraJumpOutAddress:extraJumpOutAddressList)
            shop_dataMapper.updateExtraJumpOut(extraJumpOutAddress);

    }

    //此进程用于删除三个月前的数据
    @Transactional
    @Async
    @Scheduled(cron = "* * * * 1/3 ?")
    public void deleteDataThread(){
            shop_dataMapper.deleteExpiredShop_data();
            customerMapper.deleteExpiredCustomer();
    }

}
