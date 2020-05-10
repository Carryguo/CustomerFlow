package com.carry.customerflow.bean;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Customer_Inshop {
    /**
     * 客人手机的mac
     */
    private String mac;
    /**
     * 客人的昵称
     */
    private String nickname;
    /**
     * 访问时间
     */
    private Integer visited_times;
    /**
     * 上次访问时间
     */
    private Timestamp last_in_time;
}
