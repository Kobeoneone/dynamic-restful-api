package com.sipsd.restful.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

 
//解决jmx重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class VertxWebRestfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(VertxWebRestfulApplication.class, args);
	}
	
}
