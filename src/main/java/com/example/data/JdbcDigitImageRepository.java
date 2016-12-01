package com.example.data;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import java.sql.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


@Repository// this has the same effect as @Component in terms of auto-scanning
public class JdbcDigitImageRepository implements DigitImagesRepository {
	/*
	 * No try catch or throw are well implemented now.*/
	
	
	@Autowired
	private JdbcOperations jdbcOperations;// this is an interface implemented by JdbcTemplate, which will be injected.

	@Override
	public void insertADigitImage(DigitImage dImage) {
		try{
			// Lob: large Object
			// see http://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-lob
			LobHandler lobHandler = new DefaultLobHandler();
			jdbcOperations.update(INSERT_STATEMENT,
					new Object[]{
							dImage.id,
							dImage.type,
							new SqlLobValue(dImage.data, lobHandler)
					},
					new int[]{
							Types.INTEGER,
							Types.INTEGER,
							Types.BLOB
					}
					);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public List<DigitImage> fetchDigitImages(int num) {
		List<DigitImage> list=new LinkedList<DigitImage>();
		LobHandler lobHandler = new DefaultLobHandler();
		// this part may be modified later: now I'm still testing, so I try the simplest sql command
		for (int i=0;i<num;++i){
			// use jdbcTemplate to fetch image one by one
			DigitImage dImage = jdbcOperations.queryForObject(
					SELECT_STATEMENT,
					new RowMapper<DigitImage>(){

						@Override
						public DigitImage mapRow(ResultSet rs, int rowNum) throws SQLException {
							int id = rs.getInt("id");
							int type = rs.getInt("type");
							byte[] data = lobHandler.getBlobAsBytes(rs, "data");
							return new DigitImage(id,type,data);
						}
		
					},
					i
			);
			list.add(dImage);
		}
		return list;
	}
	
	@Override
	public DigitImage getADigitImage(int id) {
		LobHandler lobHandler = new DefaultLobHandler();
		DigitImage dImage = jdbcOperations.queryForObject(
				SELECT_STATEMENT,
				new RowMapper<DigitImage>(){

					@Override
					public DigitImage mapRow(ResultSet rs, int rowNum) throws SQLException {
						int id = rs.getInt("id");
						int type = rs.getInt("type");
						byte[] data = lobHandler.getBlobAsBytes(rs, "data");
						return new DigitImage(id,type,data);
					}
	
				},
				id
		);
		return dImage;
	}
	
	@Override
	public void deleteAll() {
		//For simplicity, we combine create table here, so we drop first and then create
		jdbcOperations.execute(DROP_STATEMENT);
		jdbcOperations.execute(CREATE_STATEMENT);
	}
	
	final String INSERT_STATEMENT="INSERT INTO digits (id, type, data) VALUES (?, ?, ?);";
	final String SELECT_STATEMENT="SELECT * FROM digits where id = ?;";
	final String DROP_STATEMENT="drop table if exists digits;";
	final String CREATE_STATEMENT="create table digits (id int primary key, type int, data blob);";

	
}
