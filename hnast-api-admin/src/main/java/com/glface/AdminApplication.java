package com.glface;

import com.glface.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@ServletComponentScan
public class AdminApplication implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		SpringContextUtil.setApplicationContext(event.getApplicationContext());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AdminApplication.class, args);
		Environment env = context.getEnvironment();
		log.info("{} Starting(::{})",env.getProperty("spring.application.name"),env.getProperty("server.port"));
	}

}
