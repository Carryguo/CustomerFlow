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
    public void insertMachine(String username,String machineId, String address) {
        machineMapper.insertMachine(username,machineId,address);
    }

    @Override
    public List<Machine> findMachineByAddress(String username, String address) {
        return machineMapper.findMachineByAddress(username,address);
    }

    @Override
    public void deleteMachineByMachineId(String machineId) {
        machineMapper.deleteMachineByMachineId(machineId);
    }
}