package com.carry.customerflow.bean;

import lombok.Data;

@Data
public class Permission {
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
