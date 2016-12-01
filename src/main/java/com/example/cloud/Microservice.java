package com.example.cloud;

public class Microservice {
	public String name;
	public String URI;
	public String state;
	public static final String STATE_OFF="   ";
	public static final String STATE_ON=" * ";
	public Microservice(String name, String uRI) {
		super();
		this.name = name;
		URI = uRI;
		state=STATE_OFF;
	}
}
