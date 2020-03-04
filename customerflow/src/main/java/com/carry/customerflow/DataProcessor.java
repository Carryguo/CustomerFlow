package com.carry.customerflow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.carry.customerflow.bean.Machine;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.service.MachineService;
import com.carry.customerflow.utils.DataUtil;
import com.carry.customerflow.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Component
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
    private Map<String,Object> machineMap;
    private Map<String,Object> subMachineMap;

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
    private Integer hour_customer = 0;
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
    @Override
    public void run(String... args) throws Exception {
        try {
            //监听器
            ds = new DatagramSocket(PORT);
            System.out.println("等待链接");
            buf = new byte[1024];
            dp = new DatagramPacket(buf, buf.length);
            //系统启动时把machineId导入到redis缓存
            List<Machine> machineList = machineService.findAllMachine();
                machineMap = new HashMap<>();
            for (Machine machine:machineList) {
                subMachineMap = new HashMap<>();
                subMachineMap.put("machineId",machine.getMachineId());
                subMachineMap.put("address",machine.getAddress());
                subMachineMap.put("status",machine.getStatus());
                subMachineMap.put("rssi",machine.getRssi());
                machineMap.put(machine.getMachineId(),subMachineMap);
            }
            redisUtil.del("machine");
            redisUtil.hmset("machine",machineMap);

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
                            if (machineId.matches(idM)&&redisUtil.hget("machine",machineId)!=null){
                            //分析各个mac的数据
                                dataSize = jsonArray.size();
                                for (int i = 0; i < dataSize; i++){
                                    jsonObjectData = jsonArray.getJSONObject(i);
                                    mac = jsonObjectData.getString("mac");
                                    rssi = jsonObjectData.getInteger("rssi");
//                                    System.out.println("rssi的值:"+rssi);
                                    //筛选
                                    if (mac != null && mac.matches(macM) && rssi != null && rssi.toString().matches(rssiM)&&!mac.startsWith("00:00")){
                                        //当前时间戳
                                        latest_time = new Timestamp(System.currentTimeMillis());
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
                                                hour_customer ++;
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
                                                    customerMap.put("first_in_time",latest_time);
                                                    customerMap.put("latest_in_time",latest_time);
                                                    customerMap.put("beat",latest_time);
                                                    customerMap.put("inJudge",1);
                                                    customerMap.put("visited_times",(Integer)customerMap.get("visited_times")+1);
                                                    dataUtil.refreshCache(mac,customerMap);

                                                    customer ++;
                                                    walker ++;
                                                    hour_customer ++;
                                                    hour_in_customer ++;
                                                }else{
                                                    customerMap.put("beat",latest_time);
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
                                                        hour_customer ++;
                                                        hour_in_customer ++;
                                                        //如果还没有进过店的,视为新客人
                                                        if ((Integer)customerMap.get("visited_times")==0)
                                                        {
                                                            newIn ++;
                                                        }
                                                        customerMap.put("first_in_time",latest_time);
                                                        customerMap.put("visited_times",(Integer)customerMap.get("visited_times")+1);
                                                    }
                                                    customerMap.put("inJudge",1);
                                                    customerMap.put("latest_in_time",latest_time);
                                                    customerMap.put("beat",latest_time);
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
                                                    dataUtil.refreshCache(mac,customerMap);
                                                }
                                            }
                                        }
                                    }else
                                        continue;
                                }
                                //这里更新
                                if (customer!=0||newIn!=0||hour_in_customer!=0||walker!=0){
                                    shop_dataMapper.updateShop_data(address,customer,newIn,hour_in_customer,walker);
                                }
                                customer = 0;
                                newIn = 0;
                                hour_in_customer = 0;
                                walker = 0;
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

    @Transactional
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void dataThread(){
//        System.out.println("5秒一次");
//        Integer subHour_customer = 0;
//        //现存客流量
//        Integer dynamic_customer = 0;
//        Map<String,Object> subCustomerMap = redisUtil.hmget("customer");
//        latest_time = new Timestamp(System.currentTimeMillis());
//        for (Map.Entry<String, Object> subCustomerMap_1:subCustomerMap.entrySet()) {
//               String subMac = subCustomerMap_1.getKey();
//               Map<String,Object> subCustomerMap_2 = dataUtil.getCustomerMap(subMac);
//               Integer countTime = new Long((latest_time.getTime() - (Long)subCustomerMap_2.get("beat")) / (60*1000)).intValue();
//                if (countTime>=15&&(Integer)subCustomerMap_2.get("inJudge")==1)
//                {
//                    Long stayTime = ((Long)subCustomerMap_2.get("latest_in_time")-(Long)subCustomerMap_2.get("first_in_time"))/1000;
//                    if (stayTime<50)
//                    {
//                        jumpOut++;
//                    }
//                    subCustomerMap_2.put("inJudge",0);
//                }else if (countTime<15&&(Integer)subCustomerMap_2.get("inJudge")==1){
//                    dynamic_customer ++;
//                    subHour_customer ++;
//                }
//            dataUtil.refreshCache(subMac,subCustomerMap_2);
//        }
//        if (subHour_customer<hour_customer)
//            subHour_customer = hour_customer;
//
//        if (jumpOut!=0||dynamic_customer!=0||subHour_customer!=0)

    }



}
