package com.carry.customerflow.bean;
import lombok.Data;
import java.sql.Timestamp;
@Data
public class Customer {
    /**
     * id自增长
     */
    private Integer id;
    /**
     * mac 客户设备mac
     */
    private String mac;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * rssi 信号强度
     */
    private Integer rssi;
    /**
     * address 在哪个门店
     */
    private String address;
    /**
     * first_in_time 第一次进店的时间
     */
    private Timestamp first_in_time;
    /**
     * latest_in_time 最后一次在店的时间
     */
    private Timestamp latest_in_time;
    /**
     * beat 检测心跳
     */
    private Timestamp beat;
    /**
     * inJudge 在店检测
     */
    private Integer inJudge;
    /**
     * visited_times 访问次数
     */
    private Integer visited_times;
    /**
     * 上一次进店时间
     */
    private Timestamp last_in_time;
}
