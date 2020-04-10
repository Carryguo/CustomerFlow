package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Personal_Information;
import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.bean.User_Permission;
import com.carry.customerflow.mapper.PermissionMapper;
import com.carry.customerflow.mapper.Personal_InformationMapper;
import com.carry.customerflow.mapper.Register_ApprovalMapper;
import com.carry.customerflow.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private Register_ApprovalMapper register_approvalMapper;

    @Autowired
    private Personal_InformationMapper personal_informationMapper;

    /**
     * 根据店主名查找店名让店员选择
     * @param username
     * @return
     */
    @GetMapping("/findShopByBossName")
    public Msg findShopByBossName(@RequestParam("username") String username){
        try{
        List<Shop>  shopList = registerService.findAddressByUsername(username);
        if (shopList.size()==0){
            return Msg.failure("该店主还没有添加任何店铺,或者该店主还没有注册").setCode(401);
        }
        List<String> shopAddressList = new ArrayList<>();
        for (Shop shop:shopList) {
            shopAddressList.add(shop.getAddress());
        }
        Map<String,List<String>> shopMap = new HashMap<>();
        shopMap.put("address",shopAddressList);
            return Msg.success(shopMap);
    }catch (Exception e){
            e.printStackTrace();
            return Msg.failure("查询店铺失败").setCode(402);
        }
    }


    /**
     * 注册
     * @param uid
     * @param username
     * @param password
     * @param address
     * @return
     */
//    @PostMapping("/register")
//    public Msg register(@RequestParam("uid")String uid,@RequestParam("username")String username,@RequestParam("password")String password,@RequestParam("address")String address){
//       try{
//           if ("3".equals(uid)&&"".equals(address)){
//               return Msg.failure().setCode(401).setMessage("注册店员身份要查询店主的店铺,并且选择店铺进行绑定");
//           }
//           registerService.insertUser(uid,username,password,address);
//        }catch (DuplicateKeyException e){
//           return Msg.failure().setCode(402).setMessage("你所注册的用户名已经存在");
//        }catch (Exception e)
//       {
//           return Msg.failure().setCode(403).setMessage("注册失败，请与管理员联系");
//       }
//        return Msg.success("注册用户成功");
//    }


    @PostMapping("/register")
    public Msg register(@RequestParam("uid")String uid,@RequestParam("username")String username,@RequestParam("password")String password,@RequestParam("address")String address,@RequestParam("bossname")String bossname){
        try{
            if ("3".equals(uid)&&"".equals(address)){
                return Msg.failure().setCode(401).setMessage("注册店员身份要查询店主的店铺,并且选择店铺进行绑定");
            }else if ("3".equals(uid)&&!"".equals(address)){
                if (registerService.checkExist(username)!=0)
                    return Msg.failure().setCode(402).setMessage("你所注册的用户名已经存在");

                register_approvalMapper.inserStaffApproval(uid,username,password,address,bossname);
                return Msg.success().setCode(201).setMessage("注册成功，请等待店主的审批");
            }else
            registerService.insertUser(uid,username,password,address,bossname);

            //给出初始权限
            List<Integer> pidList = permissionMapper.searchPid(uid);
            List<User_Permission> user_permissionList = new ArrayList<>();
            for (Integer pid:pidList)
                user_permissionList.add(User_Permission.builder().username(username).pid(pid).build());
            //插入权限
            permissionMapper.insertPid(user_permissionList);

            Personal_Information personal_information = Personal_Information.builder().uid(Integer.parseInt(uid)).username(username).build();
            //初始化个人信息
            personal_informationMapper.initializePersonal_Information(personal_information);

            return Msg.success("注册用户成功");
        }catch (DuplicateKeyException e){
            return Msg.failure().setCode(402).setMessage("你所注册的用户名已经存在");
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(403).setMessage("注册失败，请与管理员联系");
        }
    }



}

