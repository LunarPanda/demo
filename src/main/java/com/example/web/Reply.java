package com.example.web;

public class Reply {
	String content;
	public Reply(){
		
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Reply(String content) {
		super();
		this.content = content;
	}
	public String toString(){
		return content;
	}
}
