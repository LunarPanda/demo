package com.example.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationRepository {
	
	@Autowired
	JdbcOperations jdbcOperations;
	
	AtomicInteger counter=new AtomicInteger();
	
	static final int MAX_LEN=256;
	
	public ConversationRepository(){
	}
	
	public void initTable(){
		jdbcOperations.execute(DROP_STATEMENT);
		jdbcOperations.execute(CREATE_STATEMENT);
	}
	
	public void addConversation(Conversation conversation){
		int id=counter.incrementAndGet();
		String request=conversation.request;
		if (request.length()>256){
			request=request.substring(0, 255);
		}
		jdbcOperations.update(INSERT_STATEMENT,id,request,conversation.reply);
		System.out.println("add a new conversation");
	}
	
	public List<Conversation> getAll(){
		List<Conversation> list=jdbcOperations.query(SELECT_STATEMENT, 
				new RowMapper<Conversation>(){

					@Override
					public Conversation mapRow(ResultSet rs, int rowNum) throws SQLException {
						//int id = rs.getInt("id");
						String request =rs.getString("request");
						String reply = rs.getString("reply");
						return new Conversation(request,reply);
					}

				});
		return list;
	}
	
	final String INSERT_STATEMENT="INSERT INTO conversations (id, request, reply) VALUES (?, ?, ?);";
	final String SELECT_STATEMENT="SELECT * FROM conversations;";
	final String DROP_STATEMENT="drop table if exists conversations;";
	final String CREATE_STATEMENT=String.format("create table conversations (id int primary key, request nvarchar(%d), reply nvarchar(%d));",MAX_LEN,MAX_LEN);
}
