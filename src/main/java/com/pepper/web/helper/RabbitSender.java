package com.pepper.web.helper;

import com.pepper.web.helper.RabbitConsumer;
import com.pepper.web.model.MqMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitSender {

    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);


    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void send(MqMsg mqMsg){
        this.send(mqMsg.getExchange(),mqMsg.getRoutingKey(),mqMsg.getData());
    }

    /**
     * 生产者调用此方法发送消息到exchange
     * @param exchange
     * @param routingKey
     * @param msg
     */
    public void send(String exchange,String routingKey,String msg){
        logger.info("开始将消息msg=[{}]根据routingKey=[{}]发送到exchange=[{}]的交换机",msg,routingKey,exchange);
        try {
            rabbitTemplate.convertAndSend(exchange,routingKey,msg);
        } catch (AmqpException e) {
            logger.info("将消息msg=[{}]根据routingKey=[{}]发送到exchange=[{}]的交换机异常",msg,routingKey,exchange);
        }
    }
}
