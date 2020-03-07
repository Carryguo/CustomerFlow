package com.carry.customerflow.bean;

import lombok.Data;

@Data
public class CustomerPerHour {
    /**
     * hour_customer_number 每个小时的客流量
     */
   private Integer hour_customer_number;
    /**
     * hours 每个小时的小时数
     */
   private Integer hours;
}
