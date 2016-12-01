package com.example.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component("shownResults")
//@RequestScope
public class ShownResults {
	
	@Autowired
	Communicator communicator;
	
	double accuracy=-1;
	void init(){
		accuracy=-1;
	}
	
	void getShownResults(){
		/*
		 * we will use methods of Communicator to send and receive result from service.*/
		Result result=communicator.getResult("db is not here now");
		this.accuracy=result.getAccuracy();
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	
}
