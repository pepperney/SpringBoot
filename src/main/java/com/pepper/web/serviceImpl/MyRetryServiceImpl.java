package com.pepper.web.serviceImpl;

import com.pepper.common.exception.NetException;
import com.pepper.common.util.DateUtil;
import com.pepper.web.model.entity.RetryTask;
import com.pepper.web.service.RetryService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MyRetryServiceImpl extends RetryService<RetryTask> {


    /**
     * 重试方法
     * 参数说明：
     * value:表示哪些异常需要重试，可多个,如{NetException.class,CustomException.class}
     * maxAttempts:最大重试次数
     * backoff:重试等待策略，默认使用@Backoff，@Backoff的value默认为1000L
     * delay:重试间隔(ms)
     * multiplier：重试间隔放大倍数
     * maxDelay：最大间隔(ms)
     *
     * @param retryTask
     */
    @Retryable(value = NetException.class, maxAttempts = 20, backoff = @Backoff(delay = 1 * 60 * 1000L, multiplier = 1, maxDelay = 2 * 60 * 60 * 1000L))
    public void retry(RetryTask retryTask) {
        logger.info("---------------进入retry()方法,当前时间为：{}", DateUtil.getFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        String result = "";
        Map<String, String> params = new HashMap<>();
        result = this.invokeRemote(retryTask);
        this.dealResponse(retryTask, result);
        logger.info("---------------离开retry()方法,当前时间为：{}", DateUtil.getFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 请求成功之后的响应处理
     *
     * @param retryTask
     * @param result
     */
    @Override
    protected void dealResponse(RetryTask retryTask, String result) {
        logger.info("---------------进入dealResponse()方法");
        // TODO 处理成功之后的业务逻辑
        retryTask.setStatus(RetryTask.STATUS_SUCCESS);
        retryTask.setUpdateTime(new Date());
        retryTaskMapper.updateByPrimaryKeySelective(retryTask);
    }

    /**
     * 重试次数达到上限之后的回调方法
     *
     * @param e
     * @param retryTask
     */
    @Recover
    public void recover(NetException e, RetryTask retryTask) {
        logger.info("任务[{}]重试失败", retryTask.getId());
        // TODO 处理失败之后的业务逻辑
        retryTask.setStatus(RetryTask.STATUS_FAIL);
        retryTask.setUpdateTime(new Date());
        retryTaskMapper.updateByPrimaryKeySelective(retryTask);
    }

}
