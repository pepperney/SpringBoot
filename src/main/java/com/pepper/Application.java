package com.pepper;

import com.pepper.web.security.AuthFilter;
import com.pepper.web.security.MessyCodeFilter;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;




@ComponentScan("com.pepper")
@EnableRetry
@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).bannerMode(Banner.Mode.OFF).web(true).run(args);
	}


	@Bean
	@Order(1)
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	}

	@Bean
	@Order(2)
	public Filter authFilter() {
		return new AuthFilter();
	}


	@Bean
	@Order(3)
	public Filter messyCodeFilter() {
		return new MessyCodeFilter();
	}


}
