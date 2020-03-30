package com.carry.customerflow.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User_Permission {
    /**
     * id
     */
    private Integer id;
    /**
     * username
     */
    private String username;
    /**
     * pid
     */
    private Integer pid;
}
