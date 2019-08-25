package com.messaging;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataRetriever {

	private Collection<User> localList = new ArrayList<User>();
	
	String url="jdbc:postgresql://localhost/dbRdF";
	String usr="postgres";
	String psw="6357";
	private QueryExecutor qe;
	
//	private static final Logger LOGGER = Logger.getLogger(QueryExecutor.class.getName());
	
	public DataRetriever(String url, String usr, String pwd) throws SQLException {
		qe = QueryExecutor.getInstance(url, usr, psw);
	}
	
	public Collection<User> refreshList(){
		try {
			localList = qe.findAll();
		} catch (SQLException e) {
			System.err.println("Data not retrieved from DB");
			e.printStackTrace();
		}
		return localList;
	}
	
	public Collection<User> findAll(){
		return findAll(null);
	}
	
	public synchronized List<User> findAll(String stringFilter) {
		ArrayList<User> arrayList = new ArrayList<>();
		for (User contact : localList) {
//			try {
			boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
					|| contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
			if (passesFilter) {
				arrayList.add(contact);
			}
//		} catch (CloneNotSupportedException ex) {
//			LOGGER.log(Level.SEVERE, null, ex);
//		}
		}
//		Collections.sort(arrayList, new Comparator<User>() {
//
//			@Override
//			public int compare(User o1, User o2) {
//				return (int) (o2.getId() - o1.getId());
//			}
//		});
		return arrayList;
	}
	
}
