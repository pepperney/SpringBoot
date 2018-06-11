package com.pepper.web.model;

import com.pepper.common.util.SerializeUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class MqMsg implements Serializable {

    private static final long serialVersionUID = -8081367116849826279L;

    private String  msgId = UUID.randomUUID().toString().replaceAll("-", "");

    private Date time = new Date();

    private String exchange;

    private String routingKey;

    private String data;


    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public MqMsg() {
    }

    public MqMsg(String exchange,String routingKey, String data) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.data = data;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getTime() {
        return time;
    }

}
