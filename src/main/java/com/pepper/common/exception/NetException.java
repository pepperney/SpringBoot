package com.pepper.common.exception;


/**
 * 重试方法指定异常类
 */
public class NetException extends RuntimeException{

    private static final long serialVersionUID = 7393177901625809193L;

    private String code;

    private String msg;

    public NetException(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }
}
