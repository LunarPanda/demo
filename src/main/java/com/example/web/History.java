package com.example.web;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.data.Conversation;

@Component
public class History {
	public List<Conversation> conversations;
	public History(){
		conversations=new LinkedList<Conversation>();

	}
	public void addConversation(Conversation conversation){
		conversations.add(conversation);
	}
}

