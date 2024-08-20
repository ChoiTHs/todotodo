package controll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TodoLogInOutController {
    static Scanner sc = new Scanner(System.in);
    static Connection conn ;

    public static String login(String name, int pwd) {
        try {
            String sql = "SELECT * FROM USERS WHERE NICKNAME = ? AND PWD = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, pwd);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {

                pstmt = conn.prepareStatement("UPDATE USERS SET STATUS = 1 WHERE NICKNAME = ?");
                pstmt.setString(1, name);
                pstmt.executeQuery();

                System.out.println("로그인 성공");
                conn.commit();
                return "로그인 성공";

            }else {
                System.out.println("로그인 실패했습니다.");
                pstmt = conn.prepareStatement("UPDATE USERS SET STATUS = 0");
                pstmt.executeQuery();
                conn.commit();
                return "로그인 실패했습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "로그인 실패했습니다.";
    }
    public static String logout(String name) {
    	try {
			String sql = "SELECT * FROM USERS WHERE NICKNAME = ? AND STATUS = 1";
			PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	pstmt = conn.prepareStatement("UPDATE USERS SET STATUS = 0 WHERE NICKNAME = ?");
                pstmt.setString(1, name);
                pstmt.executeUpdate();

                System.out.println("로그아웃 성공");
                conn.commit();
                return "로그아웃";
            } else {
            	return "로그아웃 실패했습니다.";
            }
            
		} catch (Exception e) {e.printStackTrace();}
    	return "로그아웃 실패했습니다.";
    }
}
