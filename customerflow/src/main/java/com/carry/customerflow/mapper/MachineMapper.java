package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Machine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MachineMapper {
    void insertMachine(@Param("username")String username, @Param("machineId")String machineId, @Param("address")String address,@Param("rssi")Integer rssi,@Param("leastRssi")Integer leastRssi,@Param("status")String status);
    void updateMachine(@Param("machineId")String machineId,@Param("status")String status);
    List<Machine> findMachineByAddress(@Param("username")String username,@Param("address")String address);
    void deleteMachineByMachineId(@Param("machineId")String machineId);
    List<Machine> findAllMachine();
    void deleteMachineByAddress(@Param("address")String address);
    Integer editMachine(@Param("machineId")String machineId,@Param("rssi")Integer rssi,@Param("leastRssi")Integer leastRssi);
    List<Machine> searchMachineByMachineId (@Param("machineId")String machineId,@Param("address")String address);
}
