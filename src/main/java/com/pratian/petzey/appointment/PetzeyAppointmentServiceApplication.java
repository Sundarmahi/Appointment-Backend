package com.pratian.petzey.appointment;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
		RequestMethod.PATCH }, allowedHeaders = "*", allowCredentials = "true")
@EnableEurekaClient
@SpringBootApplication
//@OpenAPIDefinition(info = @Info(title = "Appointment_Service-API", version = "1.0", description = "API for Appointment Service"), servers = {
//		@Server(url = "https://appointmentservice.bt.skillassure.com", description = "Default Server URL"),
//		@Server(url = "https://apigateway.bt.skillassure.com", description = "Eureka Server URL") })
public class PetzeyAppointmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetzeyAppointmentServiceApplication.class, args);
	}

	@Bean
	public ModelMapper getMapper() {
		return new ModelMapper();
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
