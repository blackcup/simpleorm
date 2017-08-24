package com.acvoice.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class TestDemo {

	public static void main(String[] args) throws Exception {
		String path = TestDemo.class.getClassLoader().getResource("").getPath();
		System.out.println(path);
		ConnectionPropertiesReader reader = new XmlConnectionPropertiesReader(path+"connection.xml");
		ConnectionPoolManager manager = new ConnectionPoolManager("JDBC", reader);

		Date d = new Date();
		long i = d.getTime();
		ConnectionPool pool = manager.getConnectionPool();
		long j = new Date().getTime();
		System.out.println(j-i);
		Connection con = pool.getConnection();

		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery("select * from college");
		int count = rs.getMetaData().getColumnCount();
		System.out.println("一共查询出"+count+"列");
		pool.realse(con);
		con = pool.getConnection();
		stm = con.createStatement();
		rs = stm.executeQuery("select * from college");
		count = rs.getMetaData().getColumnCount();
		System.out.println("一共查询出"+count+"列");
		System.out.println(new Date().getTime()-j);
		pool.realse(con);
	}
}
