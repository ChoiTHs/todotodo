package conn_util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Conn {
	public static Connection conn;
	public static Statement stmt;
	public static PreparedStatement pstmt;
	public static ResultSet rs;
	public static Connection getConnection(String dsn) {
		
		try {
			if(dsn.equals("mysql")) {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SampleDB", "todotodo" , "blackpink");
				}else if (dsn.equalsIgnoreCase("oracle")){
					Class.forName("oracle.jdbc.OracleDriver");
					conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "todo_test", "blackpink");
					System.out.println("connection success!!");
				}
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			return conn;
		}
	
	}
	
	public static void Close_Conn(Connection conn) {
		if (conn != null) 
			try{
				conn.close();
			} catch (SQLException e ) { 
				e.printStackTrace();
		}
	}
	
	public static void close(Statement stmt) {
		if (stmt != null) 
			try{
				stmt.close();
			} catch (SQLException e ) { 
				e.printStackTrace();
		}
	}
	
	public static void close(PreparedStatement pstmt) {
		if (pstmt != null) 
			try{
				pstmt.close();
			} catch (SQLException e ) { 
				e.printStackTrace();
		}
	}
	
	public static void close(ResultSet rs) {
		if (rs != null) 
			try{
				rs.close();
			} catch (SQLException e ) { 
				e.printStackTrace();
		}
		
	}



}
