package dbConn.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import server.MyModel;

public class UsersDao {
	//변수
	Connection conn;
	PreparedStatement pstmtUsersInsert, pstmtUsersSearch;
	PreparedStatement pstmtUsersDel, pstmtUsersUpdate;
	PreparedStatement pstmtSearchUS;
	MyModel model;
	
	//상수
	private static final int IN = 1;
	private static final String loginFail = "fail";
	String sqlUsersInsert = "INSERT INTO USERS(USERIDX, NICKNAME, PWD, STATUS) VALUES(USERS_SEQ.NEXTVAL, ?, ?, ?)";
	String sqlUsersSearch = "SELECT * FROM USERS WHERE (NICKNAME = ? AND PWD = ?)";
	String sqlUsersDel = "DELETE FROM USERS WHERE NICKNAME = ?";
	String sqlUsersSelect = "ALTER TABLE USERS WHERE STATUS = ?";
	
	// db연결
	public void dbConnect() {
        try {
            conn = ConnectionHelper.getConnection("oracle");
            
            pstmtUsersInsert = conn.prepareStatement(sqlUsersInsert);
			pstmtUsersDel= conn.prepareStatement(sqlUsersDel);
			pstmtUsersSearch = conn.prepareStatement(sqlUsersSearch);
            pstmtSearchUS= conn.prepareStatement(sqlUsersSearch, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//crud 시작
	
	// 생성
	public String createUsers(String nickname, String pwd) throws Exception {
		String str = null;
		String REGEXP_NICKNAME = "^(?=.[가-힣]|.[a-zA-Z]).{2,10}(?!.[0-9])(?!.[.@#,])$";
		String REGEXP_PWD = "^(?=.*[가-힣])(?=.*[a-zA-Z]).{2,10}$";
		dbConnect();
		pstmtUsersInsert = conn.prepareStatement(sqlUsersInsert);
//		pstmtUsersSearch = conn.prepareStatement(sqlUsersSearch);
		
		Boolean useNickname = Pattern.matches(REGEXP_NICKNAME, nickname);
		Boolean usePwd = Pattern.matches(REGEXP_PWD, pwd);
		
		if(useNickname == true && usePwd == true) {
			pstmtUsersInsert.setString(1, nickname);
			pstmtUsersInsert.setString(2, pwd);
			pstmtUsersInsert.setInt(3, 0);
			pstmtUsersInsert.executeQuery();
		}
		
		
//		pstmtSearchUS.setString(1, nickname);
//		pstmtSearchUS.setString(2, pwd);
//		ResultSet rsScroll = pstmtSearchUS.executeQuery();
//
//		pstmtUsersSearch.setString(1, nickname);
//		pstmtUsersSearch.setString(2, pwd);
//		ResultSet rs = pstmtUsersSearch.executeQuery();
//		
//		if( model == null ) model = new MyModel();
//		
//		model.getRowCount(rsScroll); 
//		model.setData(rs);
//		
//		if(model.getRows() < 1) {
//			
//		}
		
		return str;
	}
	
	// 조회
	public String login(String nickname, String pwd)  throws Exception {
		dbConnect();
		pstmtUsersSearch = conn.prepareStatement(sqlUsersSearch);
		
		
		pstmtSearchUS.setString(1, nickname);
		pstmtSearchUS.setString(2, pwd);
		ResultSet rsScroll = pstmtSearchUS.executeQuery();

		pstmtUsersSearch.setString(1, nickname);
		pstmtUsersSearch.setString(2, pwd);
		ResultSet rs = pstmtUsersSearch.executeQuery();
		
		if( model == null ) model = new MyModel();
		
		model.getRowCount(rsScroll); 
		model.setData(rs);

		String str = null;
		if(model.getRows() < 1) {
			str = loginFail;
			return str;
		}
		str = "환영합니다 " + nickname+"님.";
		updateUsers(IN);
		return str;
	}
	
	// 삭제
	public void delUsers()  throws Exception {
		dbConnect();
		pstmtUsersDel = conn.prepareStatement(sqlUsersDel);
	}
	
	// 수정
	public void updateUsers(int x)  throws Exception {
		dbConnect();
		pstmtUsersUpdate = conn.prepareStatement(sqlUsersSelect);
		
		pstmtUsersUpdate.setInt(1, x);
		System.out.println("Update user status!!!");
	}
	//crud 끝
}
