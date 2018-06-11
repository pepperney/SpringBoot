package com.pepper.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pepper.common.consts.SystemCode;
import com.pepper.common.exception.NetException;
import com.pepper.common.util.BeanUtil;
import com.pepper.common.util.HttpUtil;
import com.pepper.common.util.JsonUtil;
import com.pepper.web.dao.RetryTaskMapper;
import com.pepper.web.model.entity.RetryTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽象重试方法：建议根据业务重写dealRetryParams()方法
 *
 * @param <T>
 */
@Service
public abstract class RetryService<T> {

    protected static final Logger logger = LoggerFactory.getLogger(RetryService.class);

    @Autowired
    protected RetryTaskMapper retryTaskMapper;

    /**
     * 添加重试任务到数据库
     *
     * @param retryTask
     */
    public void addRetryTask(RetryTask retryTask) {
        retryTask.setCreateTime(new Date());
        retryTask.setUpdateTime(new Date());
        retryTaskMapper.insert(retryTask);
    }

    /**
     * 根据type和bizNo获取重试任务
     *
     * @param type
     * @param bizNo
     * @return
     */
    public RetryTask getRetryTask(String type, String bizNo) {
        return retryTaskMapper.selectRetryTask(type, bizNo);
    }

    /**
     * 构造请求参数--需根据需要决定是否重写，此处提供默认实现
     *
     * @param retryTask
     * @return
     */
    protected Map<String, String> dealRetryParams(RetryTask retryTask) {
        String reqStr = retryTask.getContent();
        JSONObject bean = JSON.parseObject(reqStr);
        return BeanUtil.beanToMap(bean);
    }

    /**
     * 处理重试返回结果
     *
     * @param data
     * @param result
     */
    protected abstract void dealResponse(T data, String result);


    /**
     * 远程方法调用
     *
     * @param retryTask
     * @return
     */
    protected String invokeRemote(RetryTask retryTask) {
        String url = retryTask.getUrl();
        if (StringUtils.isEmpty(url)) {
            logger.info("请求地址为空,任务[{}]未处理", retryTask.getId());
            return null;
        }
        String result = null;
        try {
            Map<String, String> params = dealRetryParams(retryTask);
            result = HttpUtil.get(url, params, null);
            logger.info("请求地址[{}]的入参={},响应={}", url, JsonUtil.toJson(params), result);
        } catch (RuntimeException e) {
            logger.error("请求[" + url + "]异常", e);
            throw new NetException(SystemCode.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        if (StringUtils.isBlank(result)) {
            throw new NetException(SystemCode.SYSTEM_ERROR.getCode(), "请求[" + url + "]响应数据为空");
        }
        return result;
    }


}
