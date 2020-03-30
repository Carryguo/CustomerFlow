package com.carry.customerflow.bean;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
public class Personal_Information {
    /**
     * id
     */
    private Integer id;
    /**
     * uid
     */
    private Integer uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 老板名字
     */
    private String bossname;
    /**
     * 工号
     */
    private String job_number;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机
     */
    private String phone;
    /**
     * 入职时间
     */
    private Date entry_time;
    /**
     * 密码
     */
    private String password;

    public Personal_Information (Integer id,Integer uid,String username,String bossname,String job_number,String name,String phone,Date entry_time){
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.bossname = bossname;
        this.job_number = job_number;
        this.name = name;
        this.phone = phone;
        this.entry_time = entry_time;
    }

    public Personal_Information (Integer id,Integer uid,String username,String bossname,String job_number,String name,String phone,Date entry_time,String password){
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.bossname = bossname;
        this.job_number = job_number;
        this.name = name;
        this.phone = phone;
        this.entry_time = entry_time;
        this.password = password;
    }
}
