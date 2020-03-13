package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SmokeTest {
	
	@Autowired
	private GreetingKafkaController controller;

	@Test
	void contextLoads() throws Exception{
		
		assertThat(controller).isNotNull();
	}
	
}


