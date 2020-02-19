package com.carry.customerflow.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Permission implements Serializable {
    /**
     * id
     */
    private Integer pid;
    /**
     * 权限名
     */
    private String name;
    /**
     * 权限接口
     */
    private String url;

}
