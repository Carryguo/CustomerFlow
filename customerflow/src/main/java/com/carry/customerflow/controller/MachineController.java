package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Machine;
import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.service.MachineService;
import com.carry.customerflow.utils.DataUtil;
import com.carry.customerflow.utils.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MachineController {
    //machineMap
    private Map<String,Object> machineMap;
    private Map<String,Object> subMachineMap;

    @Autowired
    private MachineService machineService;

    //redis工具类
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DataUtil dataUtil;

    private Integer rssiInt;
    /**
     * 插入设备信息
     * @param machineId
     * @param address
     * @return
     */
    @PostMapping("/insertMachine")
    public Msg insertMachine(@RequestParam("machineId")String machineId, @RequestParam("address")String address, @RequestParam("rssi")String rssi){
        try {
            rssiInt = Integer.parseInt(rssi);
            //限定rssi的格式
            if (!rssi.matches("-[1-9]\\d*")){
                return Msg.failure().setCode(402).setMessage("rssi只能设置负数");
            }else if (rssiInt<-100)
            {
                return Msg.failure().setCode(403).setMessage("rssi设置不能小于-100");
            }else if (rssiInt==0)
            {
                return Msg.failure().setCode(404).setMessage("rssi设置不能等于0");
            }
            //如果用户没有设置,则默认-50
            if (rssiInt==0){
                rssiInt=-50;
            }
            subMachineMap = new HashMap<>();
            subMachineMap.put("machineId",machineId);
            subMachineMap.put("address",address);
            subMachineMap.put("rssi",rssiInt);
            subMachineMap.put("status","离线");
            machineMap = new HashMap<>();
            machineMap.put(machineId,subMachineMap);
            redisUtil.hmset("machine",machineMap);
//            System.out.println(redisUtil.hmget("machine"));
            //这个地方到时候要从Session中获取用户名放进去
            User user = (User)SecurityUtils.getSubject().getPrincipal();
            machineService.insertMachine(user.getUsername(),machineId, address,rssiInt,"离线");
        }catch (NumberFormatException e){
            return Msg.failure().setCode(405).setMessage("rssi请设置负数");
        } catch (DuplicateKeyException e){
            return Msg.failure().setCode(401).setMessage("设备已被添加,请不要重复添加");
        }
        return Msg.success().setMessage("设备添加成功");
    }

    /**
     * 查找设备信息
     * @param address
     * @return
     */
    @GetMapping("/findMachineByAddress")
    public Msg findMachineByAddress( @RequestParam("address")String address){
        //这个地方到时候要从Session中获取用户名放进去
        try{
            User user = (User)SecurityUtils.getSubject().getPrincipal();
            List<Machine> machineList = machineService.findMachineByAddress(user.getUsername(),address);
            return Msg.success(machineList).setMessage("返回信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回设备信息失败");
        }
    }

    /**
     * 删除设备信息
     * @param machineId
     * @return
     */
    @DeleteMapping("/deleteMachineByMachineId")
    public Msg deleteMachineByMachineId(@RequestParam("machineId")String machineId){
        try{
            machineService.deleteMachineByMachineId(machineId);
            //初始化设备缓存
            dataUtil.refreshMachineCache();
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("设备删除失败");
        }
        return Msg.success().setMessage("成功删除设备");
    }
}
