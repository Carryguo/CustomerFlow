package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Permission;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.mapper.PermissionMapper;
import com.carry.customerflow.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PermissionController {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 授权
     * @param username
     * @param pid
     * @return
     */
    @PostMapping("/addPermission")
    public Msg addPermission(@RequestParam("username")String username,@RequestParam("pid")String pid){
        try{
            permissionMapper.addPermission(username,Integer.parseInt(pid));
            return Msg.success().setMessage("授权成功");
        }catch (DuplicateKeyException e){
            return Msg.failure().setCode(401).setMessage("权限已存在");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(402).setMessage("操作失败,请联系管理员");
        }
    }

    /**
     * 撤权
     * @param username
     * @param pid
     * @return
     */
    @DeleteMapping("/deletePermission")
    public Msg deletePermission(@RequestParam("username")String username,@RequestParam("pid")String pid){
        try{
            permissionMapper.deletePermission(username,Integer.parseInt(pid));
            return Msg.success().setMessage("撤权成功");
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("操作失败");
        }
    }

    /**
     * 查看权限
     * @param username
     * @return
     */
    @GetMapping("/searchPermission")
    public Msg searchPermission(@RequestParam("username")String username){
        try{

            User user = userMapper.findByUsername(username);
            //初始化所有权限
            List<Permission> permissionList = permissionMapper.searchAllPermission();
//            Map<String,Integer> permissionMap = new HashMap<>();
            for (Permission permission:permissionList)
                permission.setStatus(0);

            //标记已有的权限
            Set<Permission> permissionSet = user.getPermissions();
            for(Permission permission:permissionSet)
                permission.setStatus(permission.getPid());

            //删除相同项
            if (permissionSet.size()!=permissionList.size()){
                Iterator<Permission> iterator = permissionList.iterator();
                         while (iterator.hasNext()) {
                             Permission permission = iterator.next();
                             for(Permission sub_permission:permissionSet)
                                 if (sub_permission.getPid() == permission.getPid())
                                     iterator.remove();
                         }
            permissionList.addAll(permissionSet);
            return Msg.success(permissionList);
            }else  return Msg.success(permissionSet);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    /**
     * 查看店主的员工
     * @return
     */
    @GetMapping("/searchStaffNameByBossname")
    public Msg searchStaffNameByBossname(@RequestParam("bossname")String bossname){
        try{
            List<String> staffNameList = permissionMapper.searchStaffNamebybossname(bossname);
            return Msg.success(staffNameList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }
}
