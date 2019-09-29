package com.pepper.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @Author: pei.nie
 * @Date:2019/9/26
 * @Description:
 */
public class RetryUtil {

    private static final Logger log = LoggerFactory.getLogger(RetryUtil.class);

    public static <T, R> R doRetry(Function<T, R> function, T param, int retryTimes) throws Exception {
        if (retryTimes < 1) {
            throw new RuntimeException("retryTimes must greater than 1");
        }
        int oriRetryTimes = retryTimes;
        //只抛出最后一次的异常
        Exception exception = null;
        while (retryTimes-- > 0) {
            try {
                return function.apply(param);
            } catch (Exception e) {
                exception = e;
            }
        }
        log.error("doRetry failure,apply function:{},param={},retryTimes={}", function, param, oriRetryTimes, exception);
        throw exception;
    }


    /*public static void main(String[] args) throws Exception {
        Function<Map<String, Integer>, Integer> function = (params) -> params.get("x") / params.get("y");
        Map<String, Integer> param = new HashMap();
        param.put("x", 10);
        param.put("y", 0);
        int reteyTimes = 3;
        RetryUtil.doRetry(function, param, reteyTimes);
    }*/


}
