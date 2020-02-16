package com.carry.customerflow.bean;

import lombok.Data;


@Data
public class Shop {

    /**
     * id自增长
     */
    private Integer id;
    /**
     * username用户名字
     */
    private String username;
    /**
     * longitude经度
     */
    private String longitude;
    /**
     * latitude伟度
     */
    private String latitude;
    /**
     * address店铺地址
     */
    private String address;
}
