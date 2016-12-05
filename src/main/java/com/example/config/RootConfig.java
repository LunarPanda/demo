package com.example.config;

import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.Cloud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/*
 *  this part may be modified later, since we now only focus on web, we would make it simple.*/

@Configuration
@ComponentScan(basePackages={"com.example"}, excludeFilters={
		@Filter(type=FilterType.ANNOTATION, value=EnableWebMvc.class)})//why only this work?

public class RootConfig {
	// we put database configuration here
	// as mentioned in the notes, beans here will be shared across the whole application
	
	@Autowired
	Cloud cloud;
	
	@Profile("local")
	@Bean
	public DataSource embeddedDatabase(){
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:data/test_schema.sql")
				//.addScript("classpath:test_data.sql")
				.build();	
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {// why can't be put in WebConfig, it associates with component scan 
			return builder.build();
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}
	
}
