package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Boss_Notice;
import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.mapper.Boss_NoticeMapper;
import com.carry.customerflow.mapper.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
public class Boss_NoticeController {
    @Autowired
    private Boss_NoticeMapper boss_noticeMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 发布公告
     * @param username
     * @param notice
     * @return
     */
    @PostMapping("/editBoss_Notice")
    public Msg editBoss_Notice(@RequestParam("username")String username, @RequestParam("notice")String notice, @Param("date")Date date){
        try{
            boss_noticeMapper.editBoss_Notice(Boss_Notice.builder().username(username).notice(notice).status(0).date(date).build());
            List<User> userList = userMapper.searchStaffnameByBossname(username);
            for(User user:userList){
                boss_noticeMapper.editBoss_Notice(Boss_Notice.builder().username(user.getUsername()).notice(notice).status(1).date(date).build());
            }
            return Msg.success().setMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("操作失败");
        }
    }

    /**
     * 获取公告
     * @param username
     * @return
     */
    @GetMapping("/getBoss_Notice")
    public Msg getBoss_Notice(@RequestParam("username")String username){
        try{
            List<Boss_Notice> boss_noticeList = boss_noticeMapper.getBoss_Notice(username);
//            Boss_Notice boss_notice = new Boss_Notice();
            return Msg.success(boss_noticeList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    /**
     * 修改公告状态
     * @param username
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/changeBoss_NoticeStatus")
    public Msg changeBoss_NoticeStatus(@RequestParam("username")String username,@RequestParam("status")String status,@RequestParam("id")String id){
        try{
            boss_noticeMapper.changeBoss_NoticeStatus(username,Integer.parseInt(status),Integer.parseInt(id));
            return Msg.success();
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    @DeleteMapping("/deleteBoss_Notice")
    public Msg deleteBoss_Notice(@RequestParam("username")String username,@RequestParam("id")String id){
        try{
            boss_noticeMapper.deleteBoss_Notice(username,Integer.parseInt(id));
            return Msg.success();
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

}
