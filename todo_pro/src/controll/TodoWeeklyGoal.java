package controll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import conn_util.DB_Conn;

public class TodoWeeklyGoal {
    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    // connect
    public static void connect() {
        try {
            conn = DB_Conn.getConnection("oracle");
            
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

        } catch(Exception e) {
            e.printStackTrace();
            return "주간목표 생성 실패!";
        }
    }
    
    public static String SelectweeklyGoal(String name, String week_date) {
        StringBuilder result = new StringBuilder();
        try {
            String sql = "SELECT wg.content, wg.status,TO_CHAR(wg.createdat, 'YYYY-MM-DD') as createdat  FROM users u JOIN WEEKLY_GOAL wg ON u.userIdx = wg.writer WHERE u.nickname = ? AND wg.createdat BETWEEN TRUNC(TO_DATE(?, 'MM-DD'), 'IW') AND TRUNC(TO_DATE(?, 'MM-DD'), 'IW') + 6";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, week_date);
            pstmt.setString(3, week_date);
            ResultSet rs = pstmt.executeQuery();
            int totalGoals = 0;
            int completedGoals = 0;

            while (rs.next()) {
                String content = rs.getString("content");
                int status = rs.getInt("status");
                String weekely_date = rs.getString("createDat");
                result.append(content).append(", ").append(status == 1 ? "달성완료 \t" : "미달성 \t").append(weekely_date).append("\n");
                totalGoals++;
                if (status == 1) {
                    completedGoals++;
                }
            }

            if (totalGoals > 0) {
                int percent = (completedGoals * 100) / totalGoals;
                result.append("\n달성률은 : ").append(percent).append("% 입니다.");
            } 

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }

        return result.toString();
    }
}