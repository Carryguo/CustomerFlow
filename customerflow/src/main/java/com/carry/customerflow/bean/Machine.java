package com.carry.customerflow.bean;

import lombok.Data;

@Data
public class Machine {
    /**
     * id自增长
     */
    private Integer id;
    /**
     * username用户名
     */
    private String username;
    /**
     * machineId wifi设备id
     */
    private String machineId;
    /**
     * address 名店地址(店名)
     */
    private String address;
    /**
     * status 设备状态
     */
    private String status;
}
