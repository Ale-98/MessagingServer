package com.messaging;

public class PredefinedSQLCode {

	public static final String create_table_queries[] = {

			"create table if not exists client(" 
			+"nickname varchar(30) primary key references Messaggio(nickname)," 
			+"password varchar(16) not null"
			+");",

			"create table if not exists message("
			+"nickname varchar(30)," 
			+"dest varchar(30),"  
			+"datasend timestamp,"
			+"datareceive timestamp,"
			+"delivered boolean not null,"
			+"type char check(type in('d', 'b'))," 
			+"primary key(nickname, dest, datasend)" 
			+");"

	};

	public static final String drop_table_queries[] = {

			"drop table if exists client",
			"drop table if exists message"

	};

	public static final String insert_table_queries[] = {

			"insert into client(nickname, password, isAdmin) values(?, ?, ?)", //query parametrica
			"insert into message(nickname, dest, datasend, datareceive, delivered, type) values(?, ?, ?, ?, ?, ?)",
			"insert into date(giorno) values(?)"

	};

	public static final String select_queries[] = {

			"select nickname from client",
			"select count(*) from client",
			"select count(*) from message",
			"select count(*) from client where datasend between ? and ?",
			"select count(*) from message where datasend between ? and ?",
			"select avg(datareceive-datasend) from message where datasend between ? and ?",
			"select * from client",
			"select * from message"

	};

	public static final String delete_queries[] = {

			"delete from client where nickname=?"

	};

}
