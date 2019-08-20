package com.messaging;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DataBuffer {

	Collection<User> localList = new ArrayList<User>();
	QueryExecutor qe;
	
	public DataBuffer(QueryExecutor qe) {
		this.qe = qe;
	}
	
	public Collection<User> refreshList(){
		Collection<User> locals = new ArrayList<User>();
		try {
			locals = qe.findAll();
		} catch (SQLException e) {
			System.err.println("Data not retrieved from DB");
			e.printStackTrace();
		}
		return locals;
	}
	
}
