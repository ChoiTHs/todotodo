package controll;

import conn_util.DB_Conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TodoWeeklyGoal {
    static Connection conn;

    public static void connect() {
        try {
            conn = DB_Conn.getConnection("oracle");
            TodoLogInOutController.conn = conn;
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 주간목표 생성
    public static String insert(String userIdx, String todoContent) {
        StringBuilder result = new StringBuilder();
        int loginUser = TodoListController.findByUser(userIdx);

        try {
            PreparedStatement pstmt;
            ResultSet rs;

            System.out.println("주간목표를 생성합니다!");

            String sql = "INSERT INTO WEEKLY_GOAL(WEEKLYGOALIDX, CONTENT, STATUS, CREATEDAT, UPDATEDAT, WRITER)"
                    + "VALUES(Weekly_goal_Seq.NEXTVAL, ?, 0, SYSDATE, CURRENT_TIMESTAMP, ?)";

            pstmt = conn.prepareStatement(sql, new String[]{"WEEKLYGOALIDX"});
            pstmt.setString(1, todoContent);
            pstmt.setInt(2, loginUser);
            pstmt.executeUpdate();

            // 추가 된 주간 목표 조회
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generateWeeklyGoalIdx = rs.getInt(1);
                System.out.println("생성된 주간 목표의 인덱스: " + generateWeeklyGoalIdx);

                sql = "SELECT * FROM WEEKLY_GOAL WHERE WEEKLYGOALIDX = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, generateWeeklyGoalIdx);

                while (rs.next()) {
                    int weeklyGoalIdx = rs.getInt("weeklygoalIdx");
                    String content = rs.getString("content");
                    result.append(weeklyGoalIdx).append(",").append(content).append("\n");
                }
            }
            conn.commit();
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "주간목표 생성 실패!";
        }
    }

    //주간목표 전체 조회
    public static String selecAlltWeelyGoal(String name) {
    	StringBuilder result = new StringBuilder();
    	try {
    		String sql = "select wg.content, wg.status, wg.createdat from users u join weekly_goal wg on u.userIdx = wg.writer where u.nickName = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1,name);
    	
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
                String content = rs.getString("content");
                int status = rs.getInt("status");
                String weekely_date = rs.getString("createDat");
                result.append(content).append(", ").append(status == 1 ? "달성완료 \t" : "미달성 \t").append(", ").append(weekely_date).append("\n");
                
            }
    		
    	}catch (Exception e) {
            e.printStackTrace();
        }
    	
    	if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }

        return result.toString();
    }

    public static String selectAllWeeklyByUser(String name) {
        StringBuilder result = new StringBuilder();
        int loginUser = TodoListController.findByUser(name);
        try {
            String sql = "SELECT * FROM weekly_goal where writer=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loginUser);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String content = rs.getString("content");
                int status = rs.getInt("status");
                String weekely_date = rs.getString("createDat");
                result.append(content).append(", ").append(status == 1 ? "달성완료 \t" : "미달성 \t").append(", ").append(weekely_date).append("\n");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }
        return result.toString();
    }

    
    // 기간별 조회
    public static String SelectweeklyGoal(String name, String week_date, StringBuilder result) {
        int totalGoals = 0;
        int completedGoals = 0;

        try {
            String sql = "SELECT wg.content, wg.status, TO_CHAR(wg.createdat, 'YYYY-MM-DD') as createdat " +
                         "FROM WEEKLY_GOAL wg WHERE wg.writer = (SELECT userIdx FROM users WHERE nickname = ?) " +
                         "AND wg.createdat BETWEEN TRUNC(TO_DATE(?, 'MM-DD'), 'IW') AND TRUNC(TO_DATE(?, 'MM-DD'), 'IW') + 6";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, week_date);
            pstmt.setString(3, week_date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String content = rs.getString("content");
                int status = rs.getInt("status");
                String createdDate = rs.getString("createdat");
                result.append(content).append(", ").append(status == 1 ? "달성완료" : "미달성").append(", ").append(createdDate).append("\n");
                totalGoals++;
                if (status == 1) {
                    completedGoals++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.append("오류 발생: ").append(e.getMessage());
        }

        // 달성률 정보를 반환
        if (totalGoals > 0) {
            int percent = (completedGoals * 100) / totalGoals;
            return "달성률은 : " + percent + "% 입니다.";
        } else {
            return "달성률 정보가 없습니다.";
        }
    }

    public static String weeklyUpdate(String name, String userIdx) {
        StringBuilder result = new StringBuilder();
        int loginUser = TodoListController.findByUser(userIdx);

        try {
            PreparedStatement pstmt;
            ResultSet rs;

            String sql = "update weekly_goal set status = 1 where CONTENT = ? and writer = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, loginUser);

            int rowAffected = pstmt.executeUpdate();
            System.out.println("수정개수" + rowAffected);

            result.append(name + "가 수정되었습니다.").toString();
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            return "주간 목표가 업데이트 되지 않았습니다.";
        }

        return result.toString();
    }
}
