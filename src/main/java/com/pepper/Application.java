package com.pepper;

import com.pepper.web.security.AuthFilter;
import com.pepper.web.security.MessyCodeFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CharacterEncodingFilter;
import redis.clients.jedis.Jedis;

import javax.servlet.Filter;




@ComponentScan("com.pepper")
@EnableRetry
@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).bannerMode(Banner.Mode.OFF).web(true).run(args);
	}
}
