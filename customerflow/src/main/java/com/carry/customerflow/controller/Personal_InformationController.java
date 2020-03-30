package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Personal_Information;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.mapper.Personal_InformationMapper;
import com.carry.customerflow.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
public class Personal_InformationController {

    @Autowired
    private Personal_InformationMapper personal_informationMapper;
    @Autowired
    private UserService userService;

    /**
     * 根据用户名返回个人信息,如果没有填写,则提示填写个人信息
     * @param username
     * @return
     */
    @GetMapping("/searchPersonal_InformationByUsername")
    public Msg searchPersonal_InformationByUsername(@RequestParam("username")String username){
        try{
            Personal_Information personal_information = personal_informationMapper.searchPersonal_InformationByUsername(username);
            if (personal_information.getPhone()==null||personal_information.getName()==null)
                return Msg.success(personal_information).setCode(201).setMessage("个人信息尚未完善，请完善个人信息");
            return Msg.success(personal_information);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }
    /**
     * 返回个人信息列表
     * param = "2" 返回店主的信息,param != "2" 返回店员的信息
     * @return
     */
    @GetMapping("/searchPersonal_InformationList")
    public Msg searchPersonal_InformationList(@RequestParam("param")String param){
        try{
            List<Personal_Information> personal_informationList = personal_informationMapper.searchPersonal_InformationList(param);
            for(Personal_Information personal_information:personal_informationList){
                User user = userService.findByUsername(personal_information.getUsername());
                personal_information.setPassword(user.getPassword());
            }
            return Msg.success(personal_informationList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器异常");
        }

    }

    /**
     * 各自修改密码
     * @param username
     * @param password
     * @param newPassword
     * @return
     */
    @PostMapping("/updatePassword")
    public Msg changePassword(@RequestParam("username")String username,@RequestParam("password")String password,@RequestParam("newPassword")String newPassword){
        try{
            if(personal_informationMapper.checkUserPassword(username,password)==null){
                return Msg.failure().setCode(402).setMessage("原密码错误");
            }
            personal_informationMapper.changeUserPassword(username,newPassword);
            return Msg.success().setMessage("密码修改成功，请重新登陆");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("操作失败");
        }
    }

    @PostMapping("/editPersonal_Information")
        public Msg updatePersonal_Information(@RequestParam("username")String username, @RequestParam("job_number")String job_number, @Param("name")String name, @Param("phone")String phone, @Param("entry_time")Date entry_time){
        try{
            Personal_Information personal_information = Personal_Information.builder().job_number(job_number).name(name).phone(phone).entry_time(entry_time).build();
            personal_informationMapper.editPersonal_Information(personal_information,username);
            return Msg.success().setMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("操作失败");
        }
    }
}
