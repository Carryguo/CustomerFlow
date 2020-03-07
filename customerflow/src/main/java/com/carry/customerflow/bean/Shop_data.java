package com.carry.customerflow.bean;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class Shop_data {
    /**
     * id 自增长
     */
    private Integer id;
    /**
     * address 店铺
     */
    private String address;
    /**
     * walker_number 人流量
     */
    private Integer walker_number;
    /**
     * consumer_number 客流量
     */
    private Integer customer_number;
    /**
     * new_consumer 新客人
     */
    private Integer new_customer;
    /**
     * jump_out 跳出量
     */
    private Integer jump_out;
    /**
     * dynamic_consumer 目前还在店里的人量
     */
    private Integer dynamic_customer;
    /**
     * hour_consumer_number 小时客流量
     */
    private Integer hour_customer_number;
    /**
     * hour_in_consumer_number 小时进店客流量
     */
    private Integer hour_in_customer_number;
    /**
     * 刷新时间
     */
    private Timestamp update_time;
    /**
     * hours 每个小时
     */
    private Integer hours;
}
