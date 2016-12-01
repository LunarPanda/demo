package com.example.web;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Communicator {
	/*
	 * This class should send data to services using rest-template. Also, receive results from services.*/
	
	
	RestTemplate restTemplate;
	
	
	@Autowired
	public Communicator(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}



	//to do: add @HystrixCommand
	public Result getResult(String dbInfor){
		URI uri = UriComponentsBuilder
				.fromHttpUrl("http://127.0.0.1:8088/train")//to do: this information could be retrived from eureka or env.
				.queryParam("db", dbInfor).build().toUri();
		return restTemplate.getForObject(uri, Result.class);
	}
}

