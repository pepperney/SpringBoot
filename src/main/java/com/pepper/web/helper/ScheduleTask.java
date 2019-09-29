package com.pepper.web.helper;

import com.pepper.common.consts.Const;
import com.pepper.common.util.DateUtil;
import com.pepper.common.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务
 * (1)cron属性: 这是一个时间表达式，可以通过简单的配置就能完成各种时间的配置，我们通过CRON表达式几乎可以完成任意的时间搭配，它包含了六或七个域
 * 但是Spring的cron表达式只支持6个域，即不能设定年：
 * Seconds      : 可出现", - * /"四个字符，有效范围为0-59的整数
 * Minutes      : 可出现", - * /"四个字符，有效范围为0-59的整数
 * Hours        : 可出现", - * /"四个字符，有效范围为0-23的整数
 * DayofMonth   : 可出现", - * / ? L W C"八个字符，有效范围为0-31的整数
 * Month        : 可出现", - * /"四个字符，有效范围为1-12的整数或JAN-DEc
 * DayofWeek    : 可出现", - * / ? L C #"四个字符，有效范围为1-7的整数或SUN-SAT两个范围。1表示星期天，2表示星期一， 依次类推
 *
 * @See https://www.jianshu.com/p/c7492aeb35a1,https://www.jianshu.com/p/ef18af5a9c1d
 */
@Component
public class ScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);


    /**
     * 每小时的第0分钟开始，30分钟执行一次
     */
    private static final String SHOW_TIME_CRON = "0 0/30 * * * *";

    /**
     * 上一次调用后再次开始的时间间隔
     */
    private static final long PRINT_TIME_INTERVAL = 60 * Const.MINUTES;

    @Autowired
    private RedisHelper redisHelper;

    @Scheduled(cron = SHOW_TIME_CRON)
    public void cronTask() {
        String key = this.getClass().getName() + "@cronTask";
        String value = RandomUtil.uuid();
        // 加锁防止多实例重复执行
        if (!redisHelper.lock(key, value, 2 * Const.MINUTES)) return;
        // 添加日志轨迹
        MDC.put(Const.MDC_KEY, RandomUtil.uuid());
        logger.info("scheduleTask--[cronTask] executed,time is {}", DateUtil.getDefaultFormatDate(new Date()));
        // 解锁
        redisHelper.unLock(key, value);
    }

    @Scheduled(fixedRate = PRINT_TIME_INTERVAL)
    public void rateTask() {
        String key = this.getClass().getName() + "@rateTask";
        String value = RandomUtil.uuid();
        // 加锁防止多实例重复执行
        if (!redisHelper.lock(key, value, 2 * Const.MINUTES)) return;
        // 添加日志轨迹
        MDC.put(Const.MDC_KEY, RandomUtil.uuid());
        logger.info("scheduleTask--[rateTask] executed,time is {}", DateUtil.getDefaultFormatDate(new Date()));
        // 解锁
        redisHelper.unLock(key, value);
    }


}
