package controll;

import date_utils.DateUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TodoLogInOutController {
    static Connection conn;

    public static String login(String name, String pwd) {
        // 패스워드를 문자열로 처리
        try {
            String sql = "SELECT * FROM USERS WHERE NICKNAME = ? AND PWD = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, pwd); // 패스워드를 문자열로 설정

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
            	int status = rs.getInt("STATUS");
				if (status == 0) {
					pstmt = conn.prepareStatement("UPDATE USERS SET STATUS = 1 WHERE NICKNAME = ?");
					pstmt.setString(1, name);
					pstmt.executeUpdate(); // executeQuery()를 executeUpdate()로 변경

					conn.commit();

					StringBuilder response = new StringBuilder("로그인 성공\n\n");

					String importanceList = selectTodoListByImportance(name);
					response.append("이번달 중요한 리스트\n").append(importanceList).append("\n");

					String dayList = selectTodoListBy3datAgo(name);
					response.append("3일 남은 할일\n").append(dayList);

					return response.toString();
				} else {
					return "로그인 실패했습니다.\n\n\"사유 : 이미 로그인 되어있음";
				}

            } else {
                System.out.println("로그인 실패했습니다.");
                pstmt = conn.prepareStatement("UPDATE USERS SET STATUS = 0 WHERE NICKNAME = ?");
                pstmt.setString(1, name);
                pstmt.executeUpdate(); // executeQuery()를 executeUpdate()로 변경
                conn.commit();
                return "로그인 실패했습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "로그인 실패했습니다.";
    }

    public static String logout(String userName) {
		try {
			String sql = "SELECT * FROM USERS WHERE NICKNAME = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				pstmt = conn.prepareStatement("UPDATE USERS SET STATUS = 0 WHERE NICKNAME = ?");
				pstmt.setString(1, userName);
				pstmt.executeUpdate();

				System.out.println("로그아웃 성공");
				conn.commit();
				return "로그아웃";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    // 이번달 중요한 리스트 반환
    public static String selectTodoListByImportance(String name) {
        StringBuilder result = new StringBuilder();
        int loginUser = TodoListController.findByUser(name);

        try {
            String sql = "SELECT * FROM to_do WHERE todocreatedAt BETWEEN TRUNC(SYSDATE) "
                    + "AND TRUNC(SYSDATE) + INTERVAL '30' DAY AND IMPORTANCE = 1 AND WRITER = ? "
                    + "AND STATUS = 0";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loginUser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                Date todoCreatedAtDate = rs.getDate("todoCreatedAt");
                String todoCreatedAt = DateUtils.formatDate(todoCreatedAtDate);

                result.append("✓ ").append(title).append(" ").append(todoCreatedAt).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result.length() == 0) {
            return "조회된 결과가 없습니다.\n";
        }
        return result.toString();
    }

    // 3일남은 할일 조회
    public static String selectTodoListBy3datAgo(String name) {
        StringBuilder result = new StringBuilder();
        int loginUser = TodoListController.findByUser(name);

        try {
            String sql = "SELECT * FROM to_do WHERE todocreatedAt "
                    + "BETWEEN TRUNC(SYSDATE) AND TRUNC(SYSDATE) + INTERVAL '3' DAY AND WRITER = ? AND STATUS = 0";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loginUser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                Date sqlDate = rs.getDate("todoCreatedAt");
                String daysRemaining = DateUtils.getDaysRemaining(sqlDate);

                result.append("- ").append(title)
                        .append(" ").append(daysRemaining)
                        .append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "없음";
        }
        if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }
        return result.toString();
    }
}
