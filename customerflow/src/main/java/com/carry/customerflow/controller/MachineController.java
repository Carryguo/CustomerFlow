package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Machine;
import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MachineController {
    @Autowired
    private MachineService machineService;

    /**
     * 插入设备信息
     * @param username
     * @param machineId
     * @param address
     * @return
     */
    @PostMapping("/insertMachine")
    public Msg insertMachine(@RequestParam("username")String username,@RequestParam("machineId")String machineId, @RequestParam("address")String address){
        try {
            //这个地方到时候要从Session中获取用户名放进去
            machineService.insertMachine(username,machineId, address);
        }catch (DuplicateKeyException e){
            return Msg.failure().setCode(401).setMessage("设备已被添加,请不要重复添加");
        }
        return Msg.success().setMessage("设备添加成功");
    }

    /**
     * 查找设备信息
     * @param username
     * @param address
     * @return
     */
    @GetMapping("/findMachineByAddress")
    public Msg findMachineByAddress(@RequestParam("username")String username, @RequestParam("address")String address){
        //这个地方到时候要从Session中获取用户名放进去
        try{
            List<Machine> machineList = machineService.findMachineByAddress(username,address);
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
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("设备删除失败");
        }
        return Msg.success().setMessage("成功删除设备");
    }
}
