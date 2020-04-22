package com.carry.customerflow.service.imp;

import com.carry.customerflow.bean.Machine;
import com.carry.customerflow.mapper.MachineMapper;
import com.carry.customerflow.service.MachineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MachineServiceImp implements MachineService {
    @Resource
    private MachineMapper machineMapper;
    @Override
    public void insertMachine(String username,String machineId, String address,Integer rssi,Integer leastRssi,String status) {
        machineMapper.insertMachine(username,machineId,address,rssi,leastRssi,status);
    }

    @Override
    public List<Machine> findMachineByAddress(String username, String address) {
        return machineMapper.findMachineByAddress(username,address);
    }

    @Override
    public void deleteMachineByMachineId(String machineId) {
        machineMapper.deleteMachineByMachineId(machineId);
    }

    @Override
    public List<Machine> findAllMachine() {
        return machineMapper.findAllMachine();
    }

    @Override
    public void deleteMachineByAddress(String address) {
        machineMapper.deleteMachineByAddress(address);
    }

    @Override
    public void updateMachine(String machineId, String status) {
        machineMapper.updateMachine(machineId,status);
    }

    @Override
    public Integer editMachine(String machineId, Integer rssi, Integer leastRssi,String address) {
        return machineMapper.editMachine(machineId,rssi,leastRssi,address);
    }

    @Override
    public List<Machine> searchMachineByMachineId(String MachineId,String address) {
        return machineMapper.searchMachineByMachineId(MachineId,address);
    }

    @Override
    public List<Machine> findMachineByUsername(String username) {
        return machineMapper.findMachineByUsername(username);
    }
}
