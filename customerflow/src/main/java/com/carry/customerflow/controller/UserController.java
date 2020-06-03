package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.mapper.*;
import com.carry.customerflow.realm.AuthRealm;
import com.carry.customerflow.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private AuthRealm authRealm;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private Personal_InformationMapper personal_informationMapper;

    @Autowired
    private MachineMapper machineMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private Boss_NoticeMapper boss_noticeMapper;

    @RequestMapping("/login")
    public Msg login(){
        return Msg.failure().setCode(444).setMessage("未登录");
    }

    @RequestMapping("/unauthorized")
    public Msg unauthorized(){
        return  Msg.failure().setCode(443).setMessage("没有权限");
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(){
       User user = (User)SecurityUtils.getSubject().getPrincipal();
        System.out.println(user.getUsername());
        return "test success and username is " + user.getUsername();
    }

    @RequestMapping("/checkSession")
    @ResponseBody
    public String checkSession(){
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        System.out.println(user.getUsername());
        return "edit success";
    }

    @RequestMapping("/deleteCleanCache")
    public String cleanCache()throws Exception{
        authRealm.clearCached();
        System.out.println("清空缓存...");
        return "refuse";
    }
    @RequestMapping("/index")
    public String index(){
        return "/index";
    }


    @GetMapping("/logout")
    public Msg logout(){
        try {
            //移除这个session
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                subject.logout();
            }
            return Msg.success().setMessage("退出成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("退出失败");
        }
    }

    @RequestMapping("/admin")
    //这个注释的意思是返回数据 不用跳转页面
    @ResponseBody
    public String admin(){
        return "admin success";
    }

    @PostMapping("/loginUser")
        public Msg loginUser(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session)
    {
        String sessionId = null;
        User user;
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        //获取主体
        Subject subject = SecurityUtils.getSubject();
        try{
            subject.login(token);
            sessionId = (String) subject.getSession().getId();
            user = (User)SecurityUtils.getSubject().getPrincipal();
//            System.out.println(user);
        }catch (AuthenticationException e){
            e.printStackTrace();
            return Msg.failure("账号或密码错误").setCode(401);
        }catch (AuthorizationException e){
            e.printStackTrace();
            return Msg.failure("没有权限");
        }
        Map<String,String> map = new HashMap<>();
        map.put("token",sessionId);
        map.put("uid",user.getUid());
        map.put("address",user.getAddress());
        map.put("bossname",user.getBossname());
        return Msg.success().setData(map);
    }

    @GetMapping("/userTest")
    public void userTest(@Param("username")String username){
        System.out.println(userMapper.findByUsernameTest(username));
    }


    /**
     * 查找所有店主的信息
     * @return
     */
    @GetMapping("/searchAllBoss")
    public Msg searchAllBoss(){
        try{
            List<User> userList = userMapper.searchAllBoss();
            return Msg.success(userList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回店主失败");
        }
    }

    /**
     * 查看店员信息
     * @param bossname
     * @return
     */
    @GetMapping("/searchStaffnameByBossname")
    public Msg searchStaffnameByBossname(@RequestParam("bossname")String bossname){
        try{
            List<User> userList = userMapper.searchStaffnameByBossname(bossname);
            return Msg.success(userList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("数据返回失败");
        }
    }

    /**
     * 删除用户
     * @param uid
     * @param username
     * @return
     */
    @DeleteMapping("/deleteUser")
    public Msg deleteUser(@RequestParam("uid")String uid,@RequestParam("username")String username){
        try{
            if (uid.equals("2")){
                userMapper.deleteStaff(username);
                //删除店铺和设备
                List<Shop> shopList = shopMapper.findShopByUsername(username);
                for (Shop shop:shopList) {
                    shopMapper.deleteShop(shop.getAddress());
                    machineMapper.deleteMachineByAddress(shop.getAddress());
                }
            }
            //删除用户
            userMapper.deleteUser(username);
            //删除个人信息
            personal_informationMapper.deletePersonal_InformationByUsername(username);
            //删除店主公告
            boss_noticeMapper.deleteBoss_NoticeByUsername(username);
            //删除权限
            permissionMapper.deletePermissionByUsername(username);
            return Msg.success().setMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务错误");
        }
    }

}
