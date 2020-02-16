package com.carry.customerflow.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Msg {
    private final static int SUCCESS_CODE = 200 ;
    private final static int FAILURE_CODE = 1000 ;
    private final static String SUCCESS_MESSAGE = "请求成功";
    private final static String FAILURE_MESSAGE = "请求失败";

    private int code ;
    private Object data;
    private String message;
    private boolean success;

    public static Msg success(Object data){
        return success().setData(data);
    }

    public static Msg success(){
        return new Msg()
                .setCode(SUCCESS_CODE)
                .setSuccess(true)
                .setMessage(SUCCESS_MESSAGE);
    }

    public static Msg failure(String message){
        return failure().setData(message);
    }

    public static Msg failure(){
        return new Msg()
                .setCode(FAILURE_CODE)
                .setSuccess(false)
                .setMessage(FAILURE_MESSAGE);
    }
}
