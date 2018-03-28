package com.pepper.config.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Component;

@Component
//需要使用的时候打开注释
/*@RabbitListener(queues = "topic.rabbit")*/ 
public class Consumer1 {
	
	@RabbitHandler
    public void process(String msg) {
        System.out.println("consumer1 received : " + msg);
    }
}

