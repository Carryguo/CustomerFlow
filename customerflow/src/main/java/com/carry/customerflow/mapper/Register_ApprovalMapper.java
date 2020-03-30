package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Register_Approval;
import com.carry.customerflow.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface Register_ApprovalMapper {
    void inserStaffApproval(@Param("uid")String uid,@Param("username")String username,@Param("password")String password,@Param("address")String address,@Param("bossname")String bossname);
    void admitStaffApproval(@Param("username")String username,@Param("bossname")String bossname);
    void deleteStaffApproval(@Param("username")String username,@Param("bossname")String bossname);
    List<Register_Approval> searchRegister_ApprovalByBossname(@Param("bossname")String bossname);
}
