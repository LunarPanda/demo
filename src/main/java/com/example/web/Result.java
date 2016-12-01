package com.example.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result{
	double accuracy;
	
	public Result(){
		accuracy= -0.01;
	}

	public Result(double accuracy) {
		super();
		this.accuracy = accuracy;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
}