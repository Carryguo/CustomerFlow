package com.carry.customerflow.bean;

import lombok.Data;

@Data
public class Register_Approval {
    /**
     * id
     */
    private Integer id;
    /**
     * uid
     */
    private Integer uid;
    /**
     * 店员用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 绑定地址
     */
    private String address;
    /**
     * 绑定的店主名
     */
    private String bossname;

}
