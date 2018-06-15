package com.pepper.web.helper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);

    /**
     * routingKey = key.topic.test.1
     * @param msg
     */
    @RabbitListener(queues = "queue_test_1", containerFactory = "rabbitListenerContainerFactory")
    public void handleRabbitMsg(@Payload String msg) {
        logger.info("--------->获取到队列[queue_test1]的通知消息内容msg={}",msg);
        // TODO
        try {

        }catch (Exception e){
            logger.error("--------->处理队列[queue_test1]的通知消息内容异常",e);
        }
    }


    /**
     * routingKey = key.topic.test.*
     * @param msg
     */
    @RabbitListener(queues = "queue_test_2", containerFactory = "rabbitListenerContainerFactory")
    public void handleRocketMsg(@Payload String msg) {
        logger.info("--------->获取到队列[queue_test2]的通知消息内容msg={}",msg);
        // TODO
        try {

        }catch (Exception e){
            logger.error("--------->处理队列[queue_test2]的通知消息内容异常",e);
        }
    }
}
