package controll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import conn_util.DB_Conn;


public class Todo_Controller {
    static Connection conn;
    static Scanner sc = new Scanner(System.in);
    static PreparedStatement pstmt;

    // connect
    public static void connect() {
        try {
            conn = DB_Conn.getConnection("oracle");
            TodoLogInOutController.conn = conn;
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    회원가입
     */
    public static String insert(String name, String pwd) {
        try {

            String sql = "INSERT INTO USERS (USERIDX, NICKNAME, CREATEDAT, UPDATEDAT, PWD, STATUS) VALUES (Users_SEQ.NEXTVAL, ?, SYSDATE, CURRENT_TIMESTAMP, ?, 0)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // 닉네임 입력 및 유효성 검사
            String name_reg = "^(?![0-9])[a-zA-Z가-힣0-9]*$";

            // 유효성 검사
            if (!name.matches(name_reg)) {
                return "다시 입력해주세요. (특수문자 x, 맨 앞에 숫자 x)";
            }

            // 닉네임 중복 체크
            String checkSql = "SELECT * FROM USERS WHERE NICKNAME = ?";
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setString(1, name);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next()) {
                return "이미 있는 닉네임입니다. 다시 입력해주세요.";
            }

            String pwd_reg = "^\\d{4}$";
           

            if (!pwd.matches(pwd_reg)) {
                return "다시 입력해주세요. (숫자만으로 이뤄진 4자리)";
            }
            pstmt.setString(1, name);
            pstmt.setString(2, pwd);
            pstmt.executeUpdate();
            conn.commit();
            return "환영합니다!!!!!! ❤️" + name + "님❤️";

        } catch (Exception e) {
            e.printStackTrace();
            return "회원가입 실패";
        }
    }

    public static String selectAll() {
        StringBuilder result = new StringBuilder();
        try {
            pstmt = conn.prepareStatement("SELECT * FROM USERS");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nickname = rs.getString("nickname");
                String pwd = rs.getString("pwd");
                String status = rs.getString("STATUS");

                result.append(nickname).append(", ").append(pwd).append(", ").append(status).append("\n");

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "없음";
        }

        if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }

        return result.toString();
    }

    // 로그인/ 로그아웃
    public static String login(String name, String pwd) {
        String str = TodoLogInOutController.login(name, pwd);

        return str;
    }
    public static String logout(String user) {
    	String str = TodoLogInOutController.logout(user);
    	
    	return str;
    }

    // 서버
    public static void server() {
        TodoServer.server();

    }
}