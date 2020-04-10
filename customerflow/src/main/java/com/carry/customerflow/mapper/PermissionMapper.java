package com.carry.customerflow.mapper;

import com.carry.customerflow.bean.Permission;
import com.carry.customerflow.bean.User_Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper {
    List<Integer> searchPid(@Param("uid") String uid);
    void insertPid(List<User_Permission> user_permissionsList);
    void addPermission(@Param("username")String username,@Param("pid")Integer pid);
    void deletePermission(@Param("username")String username,@Param("pid")Integer pid);
    List<Permission> searchAllPermission();
    List<String> searchStaffNamebybossname(@Param("bossname")String bossname);
    void deletePermissionByUsername(@Param("username")String username);
}
