package com.carry.customerflow.service;


import com.carry.customerflow.bean.Machine;

import java.util.List;

public interface MachineService {
    void insertMachine(String username,String machineId, String address);
    List<Machine> findMachineByAddress(String username, String address);
    void deleteMachineByMachineId(String machineId);
}