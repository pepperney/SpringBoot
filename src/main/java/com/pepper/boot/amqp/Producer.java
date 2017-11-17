package com.pepper.boot.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void send() {
		String msg1 = " this is message from rabbit queue ";
		System.out.println("sender1 : " + msg1);
		this.rabbitTemplate.convertAndSend("exchange", "topic.rabbit", msg1);

		String msg2 = " this is message from rocket queue ";
		System.out.println("sender2 : " + msg2);
		this.rabbitTemplate.convertAndSend("exchange", "topic.rocket", msg2);
	}
}