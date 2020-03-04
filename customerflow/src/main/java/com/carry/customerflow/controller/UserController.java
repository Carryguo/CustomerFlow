package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.realm.AuthRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private AuthRealm authRealm;
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

    @RequestMapping("/logout")
    public String logout(){
        //移除这个session
        Subject subject = SecurityUtils.getSubject();
        if (subject != null)
        {
            subject.logout();
        }
        return "/logout";
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
        }catch (AuthenticationException e){
            e.printStackTrace();
            return Msg.failure("账号或密码错误");
        }catch (AuthorizationException e){
            e.printStackTrace();
            return Msg.failure("没有权限");
        }
        Map<String,String> map = new HashMap<>();
        map.put("token",sessionId);
        map.put("uid",user.getUid());
        return Msg.success().setData(map);
    }
}
