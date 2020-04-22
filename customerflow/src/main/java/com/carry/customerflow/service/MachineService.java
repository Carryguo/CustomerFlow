package com.carry.customerflow.service;


import com.carry.customerflow.bean.Machine;

import java.util.List;

public interface MachineService {
    void insertMachine(String username,String machineId, String address,Integer rssi,Integer leastRssi,String status);
    List<Machine> findMachineByAddress(String username, String address);
    void deleteMachineByMachineId(String machineId);
    List<Machine> findAllMachine();
    void deleteMachineByAddress(String address);
    void updateMachine(String machineId,String status);
    Integer editMachine(String machineId,Integer rssi, Integer leastRssi,String address);
    List<Machine> searchMachineByMachineId (String MachineId,String address);
    List<Machine> findMachineByUsername(String username);
}
