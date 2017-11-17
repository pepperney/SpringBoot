package com.pepper.boot.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "topic.rocket")
public class Consumer2 {
	
	@RabbitHandler
    public void process(String msg) {
        System.out.println("consumer2 received : " + msg);
    }
}

