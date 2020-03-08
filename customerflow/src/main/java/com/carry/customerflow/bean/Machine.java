package com.carry.customerflow.bean;

import lombok.Data;

import java.sql.Timestamp;

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
    /**
     * rssi 设备信号强度
     */
    private Integer rssi;
    /**
     * leastRssi 最小限制Rssi
     */
    private Integer leastRssi;
    /**
    * beat 更新时间
    */
    private Timestamp beat;
}
