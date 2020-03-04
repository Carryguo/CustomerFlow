package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Machine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MachineMapper {
    void insertMachine(@Param("username")String username, @Param("machineId")String machineId, @Param("address")String address,@Param("rssi")Integer rssi,@Param("status")String status);
    List<Machine> findMachineByAddress(@Param("username")String username,@Param("address")String address);
    void deleteMachineByMachineId(@Param("machineId")String machineId);
    List<Machine> findAllMachine();
}
