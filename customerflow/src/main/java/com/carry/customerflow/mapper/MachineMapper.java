package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Machine;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MachineMapper {
    void insertMachine(@Param("username")String username, @Param("machineId")String machineId, @Param("address")String address);
    List<Machine> findMachineByAddress(@Param("username")String username,@Param("address")String address);
    void deleteMachineByMachineId(@Param("machineId")String machineId);
}
