package com.example.config;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages={"com.example.web"})
public class WebConfig extends WebMvcConfigurerAdapter {
	  @Override
	  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		  	configurer.enable();
	  }
	  
	  @Bean
	  public RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
	  }
}
