package org.Server.Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC_Unit {
	public static Connection getConnection() throws SQLException {
		Connection c = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mySQL://localhost:3306/pbl4";
			String username = "root";
			String password = "568389";
			
			c = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}catch(SQLException e) {
			System.out.println("khong the ket noi co so du lieu!! : " + e);
			throw(e);
		}
		return c; 
	}
	
	public static Connection getConnection(String url, String username, String password) throws SQLException {
		Connection c = null;
		
		try {
			
			c = DriverManager.getConnection(url, username, password);
			
		}catch(SQLException e) {
			System.out.println("khong the ket noi co so du lieu!! : " + e);
			throw(e);
		}
		return c; 
	}
	
	public static void closeConnection(Connection c) {
		try {
			if(c != null) {
				c.close();
			}
		}catch(Exception e) {
		}
	}
}
