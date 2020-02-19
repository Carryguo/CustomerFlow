package com.carry.customerflow.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class Role implements Serializable {
    /**
     * 角色id
     */
    private Integer rid;
    /**
     * 角色名
     */
    private String rname;
    /**
     * 对应多个权限接口
     */
    private Set<Permission> permissions = new HashSet<>();
    /**
     * 一个角色对应多个用户
     */
    private Set<User> users = new HashSet<>();
}
