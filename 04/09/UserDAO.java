package user;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import util.security.AES256;
import util.security.SecretMyKey;

public class UserDAO {
	private DataSource ds; // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool) 이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	
	
	public UserDAO() {
		try {
			Context initContext = new InitialContext();
		    Context envContext  = (Context)initContext.lookup("java:/comp/env");
		    ds = (DataSource)envContext.lookup("jdbc/mysql_oracle");
		    
		    
		    
		}catch(NamingException e) {
			e.printStackTrace();
	
		}
		
		/*
		 * try { String dbURL = "jdbc:mysql://localhost:1521/mysql_user"; String dbID =
		 * "mysql_user"; String dbPassword = "cclass";
		 * Class.forName("com.mysql.jdbc.Driver"); conn =
		 * DriverManager.getConnection(dbURL, dbID, dbPassword);
		 * 
		 * 
		 * }catch(Exception e) { e.printStackTrace(); }
		 */
	}
	// 4. 자원반납 해주는 메소드
	private void close() {
		
		try {
			if(rs != null) {rs.close(); rs=null;}
			if(pstmt != null) {pstmt.close(); pstmt=null;}
			if(conn != null) {conn.close(); conn=null;}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}// end of private void close()-----------------
	
	public ArrayList<User> search(String userName){
		String SQL = "select * from user where userName LIKE ? ";
		ArrayList<User> userList = new ArrayList<User>();
		
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1,  "%"+userName+"%");
			
			rs= pstmt.executeQuery();
			while(rs.next()) {
				User user = new User();
				user.setUserName(rs.getString(1));
				user.setUserAge(rs.getInt(2));
				user.setUserGender(rs.getString(3));
				user.setUserEmail(rs.getString(4));
				userList.add(user);
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return userList;
	}
}
