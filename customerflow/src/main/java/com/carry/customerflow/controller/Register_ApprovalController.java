package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Personal_Information;
import com.carry.customerflow.bean.Register_Approval;
import com.carry.customerflow.bean.User_Permission;
import com.carry.customerflow.mapper.PermissionMapper;
import com.carry.customerflow.mapper.Personal_InformationMapper;
import com.carry.customerflow.mapper.Register_ApprovalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Register_ApprovalController {
    @Autowired
    private Register_ApprovalMapper register_approvalMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private Personal_InformationMapper personal_informationMapper;

    /**
     * 同意审批
     * @param username
     * @param bossname
     * @return
     */
    @PostMapping("/agreeRegister_Approval")
    public Msg agreeRegister_Approval(@RequestParam("username")String username,@RequestParam("bossname")String bossname){
        try{
            register_approvalMapper.admitStaffApproval(username,bossname);

            //权限设置
            List<Integer> pidList = permissionMapper.searchPid("3");
            List<User_Permission> user_permissionList = new ArrayList<>();
            for (Integer pid:pidList)
                user_permissionList.add(User_Permission.builder().username(username).pid(pid).build());
            //插入权限
            permissionMapper.insertPid(user_permissionList);
            //删除申请
            register_approvalMapper.deleteStaffApproval(username,bossname);
            //初始化个人信息
            Personal_Information personal_information = Personal_Information.builder().uid(Integer.parseInt("3")).username(username).bossname(bossname).build();
            personal_informationMapper.initializePersonal_Information(personal_information);

            return Msg.success().setMessage("操作成功");
        }catch (DuplicateKeyException e){
            return Msg.failure().setCode(401).setMessage("注册失败，该用户已被注册");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(402).setMessage("操作失败");
        }
    }

    /**
     * 拒绝审批
     * @param username
     * @param bossname
     * @return
     */
        @PostMapping("/refuseRegister_Approval")
    public Msg refuseRegister_Approval(@RequestParam("username")String username,@RequestParam("bossname")String bossname){
        try{
            register_approvalMapper.deleteStaffApproval(username,bossname);
            return Msg.success().setMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("操作失败");
        }
    }

    /**
     * 根据店主的名字返回注册审批信息
     * @param bossname
     * @return
     */
    @GetMapping("/searchRegister_ApprovalByBossname")
    public Msg searchRegister_ApprovalByBossname(@RequestParam("bossname")String bossname){
        try{
            List<Register_Approval> register_approvalList = register_approvalMapper.searchRegister_ApprovalByBossname(bossname);
            return Msg.success(register_approvalList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }
}
