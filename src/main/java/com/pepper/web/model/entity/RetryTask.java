package com.pepper.web.model.entity;

import java.io.Serializable;
import java.util.Date;

public class RetryTask implements Serializable {

    private static final long serialVersionUID = 8948789368501439529L;

    public static final int STATUS_DEFAULT = 10; //默认
    public static final int STATUS_FAIL = 20;  //失败
    public static final int STATUS_SUCCESS = 30;  //成功


    /** id */
    private Long id;

    /**类型**/
    private String type;

    /**业务编码**/
    private String bizNo;

    /**url**/
    private String url;

    /**内容**/
    private String content;

    /**状态**/
    private Integer status;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
