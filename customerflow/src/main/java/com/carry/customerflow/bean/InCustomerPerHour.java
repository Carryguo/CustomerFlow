package com.carry.customerflow.bean;

import lombok.Data;

@Data
public class InCustomerPerHour {
    /**
     * hour_in_customer_number 每小时的进店量
     */
    private Integer hour_in_customer_number;
    /**
     * hours 每个小时的小时数
     */
    private Integer hours;
}
