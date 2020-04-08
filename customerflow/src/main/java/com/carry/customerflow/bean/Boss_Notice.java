package com.carry.customerflow.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
//@NoArgsConstructor
public class Boss_Notice {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 公告
     */
    private String notice;
    /**
     * 已读状态
     */
    private Integer status;
    /**
     * 日期
     */
    private Timestamp date;
}
