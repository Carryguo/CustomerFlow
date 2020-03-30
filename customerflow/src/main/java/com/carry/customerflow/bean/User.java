package com.carry.customerflow.bean;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

@Data
public class User implements Serializable  {
    /**
     * id
     */
    private String uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 店员所绑定的店面
     */
    private String address;
    /**
     * 老板的名字
     */
    private String bossname;
    /**
     * 一个用户对应多个角色
     */
//    private Set<Role> roles = new HashSet<>();
    /**
     * 一个用户对应多个权限
      */
    private Set<Permission> permissions = new HashSet<>();
}
