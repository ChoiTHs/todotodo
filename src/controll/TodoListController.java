package controll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import conn_util.DB_Conn;

public class TodoListController {
    static Connection conn;
    static Scanner sc = new Scanner(System.in);


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

    public static String insert(String userIdx, String todo, int categoryIdx, String date, String importance) {
        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);

        try {
            PreparedStatement pstmt;
            ResultSet rs;

            System.out.println("할 일을 생성합니다!");

            String sql =
                    "INSERT INTO TO_DO (TODOIDX, WRITER, TODOCREATEDAT, TITLE, CATEGORYIDX, STATUS, IMPORTANCE , CREATEDAT, UPDATEDAT) "
                            + "VALUES(To_do_Seq.NEXTVAL, ?, To_Date(?, 'YYYY-MM-DD'), ?, ?, 0,?, SYSDATE, CURRENT_TIMESTAMP)";

            pstmt = conn.prepareStatement(sql, new String[]{"TODOIDX"});
            pstmt.setInt(1, loginUser);
            pstmt.setString(2, "2024-" + date);
            pstmt.setString(3, todo);
            pstmt.setInt(4, categoryIdx);
            pstmt.setString(5, importance);
            pstmt.executeUpdate();

            // 추가된 할일 조회
            rs = pstmt.getGeneratedKeys();     // insert 로 자동 생성된 키 가져옴
            if (rs.next()) {
                int generateTodoIdx = rs.getInt(1);
                System.out.println("생성된 할 일의 인덱스: " + generateTodoIdx);

                sql = "SELECT * FROM TO_DO WHERE TODOIDX = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, generateTodoIdx);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    int todoIdx = rs.getInt("todoIdx");
                    String title = rs.getString("title");
                    String todoCreatedAt = rs.getString("todoCreatedAt");

                    result.append(todoIdx).append(",").append(title).append(",").append(todoCreatedAt).append("\n");
                }
            }

            conn.commit();
            return "오늘의 할 일 생성 성공";

        } catch (Exception e) {
            e.printStackTrace();
            return "오늘의 할 일 생성 실패!";
        }
    }


    // 할 일 삭제
    public static String delete(String title_todo, String userIdx) {

        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);

        try {
            String sql = "delete from to_do where title = ? and writer =?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title_todo);
            pstmt.setInt(2, loginUser);

            ResultSet rs = pstmt.executeQuery();

            result.append(title_todo + "가 삭제되었습니다.").toString();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            return "게시글이 삭제 되지 않았습니다.";
        }
        return result.toString();
    }

    public static String updateStatus(String title, String userIdx) {
        int loginUser = findByUser(userIdx);
        try {
            String sql = "UPDATE TO_DO SET UPDATEDAT = SYSDATE, STATUS = 1 WHERE title = ? AND WRITER = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setInt(2, loginUser);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "할 일의 달성 여부가 변경되었습니다";
    }

    public static String updateTitle(String userIdx, String title, String newTitle) {

        int loginUser = findByUser(userIdx);

        try {
            String sql = "UPDATE TO_DO SET title = ?, UPDATEDAT = SYSDATE WHERE WRITER = ? AND TITLE = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newTitle);
            pstmt.setInt(2, loginUser);
            pstmt.setString(3, title);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "할 일의 이름이 변경되었습니다";
    }
    
 // 투두 전체 조회
    public static String selectAllTodo(String userIdx) {
    	StringBuilder result = new StringBuilder();
    	int loginUser = findByUser(userIdx);
    	try {
    		PreparedStatement pstmt;
        	ResultSet rs;
        	
        	String selectSql = "select td.title, c.name, td.status, td.todocreatedat, td.importance from to_do td join category c on td.categoryIdx = c.categoryIdx  where td.writer = ? order by td.todocreatedat";
        	pstmt = conn.prepareStatement(selectSql);
        	pstmt.setInt(1,loginUser);
        	rs = pstmt.executeQuery();
        	
        	while (rs.next()) {
				String title = rs.getString("title");
				String name = rs.getString("name");
				int status = rs.getInt("status");
				String todocreatedat = rs.getString("todocreatedat");
				int importance = rs.getInt("importance");
				
				result.append(title).append(",").append(name).append(",").append(status == 1 ? " 완료" : " 미완료").append(", ").append(todocreatedat).append(", ").append(importance  == 1 ? "✔️중요" : " ").append("\n");
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if (result.length() == 0) {
	        return "조회된 결과가 없습니다.";
	    }
	    
		  return result.toString();
    	
    }
    

    //카테고리별 조회
    public static String selectByCotegory(String name, String category) {
        StringBuilder result = new StringBuilder();
        try {
            String sql = "select td.title, td.importance, TO_CHAR(td.createdAt, 'YYYY-MM-DD') AS createdAt, td.status, TO_CHAR(td.todocreatedAt, 'YYYY-MM-DD') AS todocreatedAt  from users u join to_do td on u.userIdx = td.writer join category c on td.categoryIdx = c.categoryIdx where u.nickname = ? and c.name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                int importance = rs.getInt("importance");
                String status = rs.getString("STATUS");
                String todocreatedAt = rs.getString("todocreatedAt");

                result.append(title).append(",")
                        .append(importance == 1 ? "🌟" : " ")
                        .append(",").append(status)
                        .append(",").append(todocreatedAt)
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 결과가 없을 경우
        if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }

        return result.toString();
    }

    // 날짜별 조회
    public static String selectByUserDateTodo(String name, String tododate) {
        StringBuilder result = new StringBuilder();
        try {
            String sql = "select td.title, td.status, td.importance, TO_CHAR(td.todocreatedAt, 'YYYY-MM-DD') AS todocreatedAt from users u join to_do td on u.userIdx = td.writer join category c on td.categoryIdx = c.categoryIdx  where u.nickname = ?  and TO_CHAR(td.todocreatedAt, 'yyyy-MM-DD') = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, tododate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                int importance = rs.getInt("importance");
                int status = rs.getInt("STATUS");
                String todocreatedAt = rs.getString("todocreatedAt");

                result.append(title).append(",")
                        .append(importance == 1 ? "🌟" : " ")
                        .append(",").append(status == 1? "완료" : "미완료")
                        .append(",").append(todocreatedAt)
                        .append("\n");
                System.out.println(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result.length() == 0) {
            return "조회된 결과가 없습니다.";
        }

        return result.toString();
    }


    // 상태값에 따른 사용자 할 일 리스트 조회
    public static String SelectTodoByUserAndStatus(String userIdx, String statusInt) {
        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);
        try {
        	String sql = "select td.title, c.name, td.status, td.importance, TO_CHAR(td.todocreatedAt, 'YYYY-MM-DD') AS todocreatedAt from users u join to_do td on u.userIdx = td.writer join category c on td.categoryIdx = c.categoryIdx  where td.writer = ?  and td.status = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loginUser);
            pstmt.setString(2, statusInt);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
				String title = rs.getString("title");
				String status_name = rs.getString("name");
				int status = rs.getInt("STATUS");	
				int importance = rs.getInt("importance");
				String todocreatedAt = rs.getString("todocreatedAt");
			
				
				result.append(title).append(", ").append(status_name).append(", ").append( status == 1 ? "완료" : "미완료 ").append(", ").append(importance  == 1 ? "✔️" : " ").append(", " ).append(todocreatedAt). append("\n");
				
				
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

    // 유저 조회
    public static int findByUser(String name) {
        try {
            PreparedStatement pstmt;

            pstmt = conn.prepareStatement("SELECT * FROM USERS WHERE NICKNAME = ?");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userIdx = rs.getInt("userIdx");
                return userIdx;
            } else {
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 월별 할일 조회
    public static String selectTodosByMonth(String userIdx, int year, int month) {
        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);
        try {
            // 날짜 형식 지정 및 월 조회 쿼리
            String sql = "SELECT title, status, importance, TO_CHAR(todocreatedAt, 'YYYY-MM-DD') AS todocreatedAt " +
                    "FROM TO_DO " +
                    "WHERE WRITER = ? " +
                    "AND TO_CHAR(todocreatedAt, 'YYYY') = ? " +
                    "AND TO_CHAR(todocreatedAt, 'MM') = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loginUser);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                int importance = rs.getInt("importance");
                String status = rs.getString("status");
                String todocreatedAt = rs.getString("todocreatedAt");

                result.append(title).append(", ")
                        .append(importance == 1 ? "✔️" : " ").append(", ")
                        .append(status).append(", ")
                        .append(todocreatedAt).append("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "조회 중 오류가 발생했습니다.";
        }

        if (result.length() == 0) {
            return "현재 월에 할 일이 없습니다.";
        }

        return result.toString();
    }

	

}
