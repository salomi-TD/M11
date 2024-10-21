package com.mobile.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MobileAppServiceApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(MobileAppServiceApplication.class, args);
	}

}
