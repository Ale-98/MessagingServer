package com.messaging;

public class PredefinedSQLCode {

	public static final String create_table_queries[] = {

			"create table if not exists client(" 
			+"nickname varchar(30) primary key," 
			+"password varchar(16) not null,"
			+"subdate timestamp not null"
			+");",

			"create table if not exists message("
			+"nickname varchar(30)," 
			+"text varchar(100) not null,"
			+"dest varchar(30) not null,"  
			+"datasend timestamp,"
			+"latency_ms numeric not null,"
			+"delivered boolean not null,"
			+"type varchar(10) not null check(type in('direct', 'broadcast'))," 
			+"primary key(nickname, datasend)" 
			+");"

	};

	public static final String drop_table_queries[] = {

			"drop table if exists client",
			"drop table if exists message"

	};

	public static final String insert_table_queries[] = {

			"insert into client(nickname, password, subdate) values(?, ?, ?)", //query parametrica
			"insert into message(nickname, text, dest, datasend, latency_ms, delivered, type) values(?, ?, ?, ?, ?, ?, ?)"

	};

	public static final String select_queries[] = {

			"select nickname from client",
			"select count(*) from client",
			"select count(*) from message",
			"select count(*) from client where subdate between ? and ?",
			"select count(*) from message where datasend between ? and ?",
			"select avg(latency_ms) from message where latency_ms != 0 and datasend between ? and ?",
			"select nickname, text, dest, datasend from message where dest = ? and delivered = false",
			"select * from client",
			"select * from message"

	};

	public static final String delete_queries[] = {

			"delete from client where nickname=?"

	};

	public static final String update_queries[] = {
			
			"update message set delivered = true where dest = ? and delivered = false"
			
	};
}
