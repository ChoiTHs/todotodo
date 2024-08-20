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

    // 할 일 생성
    public static String insert(String userIdx, String date, int categoryIdx, String todo) {
        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);

        try {
            PreparedStatement pstmt;
            ResultSet rs;

            System.out.println("할 일을 생성합니다!");

            String sql =
                    "INSERT INTO TO_DO (TODOIDX, WRITER, TODOCREATEDAT, TITLE, CATEGORYIDX, STATUS, CREATEDAT, UPDATEDAT) "
                            + "VALUES(To_do_Seq.NEXTVAL, ?, TO_DATE(?, 'yyyy-mm-dd'), ?, ?, 0, SYSDATE, CURRENT_TIMESTAMP)";

            pstmt = conn.prepareStatement(sql, new String[]{"TODOIDX"});
            pstmt.setInt(1, loginUser);
            pstmt.setString(2, "2024-" + date);
            pstmt.setString(3, todo);
            pstmt.setInt(4, categoryIdx);
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
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "오늘의 할 일 생성 실패!";
        }
    }


    // 할 일 삭제
    public static String delete(int list_index, String userIdx) {

        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);

        try {
            String sql = "delete from to_do where todoidx = ? and writer =?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, list_index);
            pstmt.setInt(2, loginUser);

            ResultSet rs = pstmt.executeQuery();

            result.append(list_index + "가 삭제되었습니다.").toString();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            return "게시글이 삭제 되지 않았습니다.";
        }
        return result.toString();
    }

    public static String updateStatus(int status, String title, String userIdx) {
			int loginUser = findByUser(userIdx);
	    	try {
			String sql = "UPDATE TO_DO SET UPDATEDAT = SYSDATE, STATUS = ? WHERE title = ? AND WRITER = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, status);
				pstmt.setString(2, title);
				pstmt.setInt(3, loginUser);
				pstmt.executeUpdate();
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
			return "할 일의 달성 여부가 변경되었습니다";
		}

    public static String updateTitle(String title, String todoTitle, String userIdx) {
			
			int loginUser = findByUser(userIdx);
			
			try {
			String sql = "UPDATE TO_DO SET title = ?, UPDATEDAT = SYSDATE WHERE title = ? AND WRITER = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, title);
				pstmt.setString(2, todoTitle);
				pstmt.setInt(3, loginUser);
				pstmt.executeUpdate();
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "할 일의 이름이 변경되었습니다";
		}
    
    //카테고리별 조회
    public static String selectByCotegory(String name,String category) {
		StringBuilder result = new StringBuilder();
		try {
			String sql = "select td.title, TO_CHAR(td.createdAt, 'YYYY-MM-DD') AS createdAt, td.status, TO_CHAR(td.todocreatedAt, 'YYYY-MM-DD') AS todocreatedAt  from users u join to_do td on u.userIdx = td.writer join category c on td.categoryIdx = c.categoryIdx where u.nickname = ? and c.name = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, category);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String title = rs.getString("title");
				String createdAt = rs.getString("createdAt");
				String status = rs.getString("STATUS");	
				String todocreatedAt = rs.getString("todocreatedAt");
				result.append(title).append(", ").append(createdAt).append(", ").append(status).append(", ").append(todocreatedAt). append("\n");
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			String sql = "select td.title, td.status, TO_CHAR(td.todocreatedAt, 'YYYY-MM-DD') AS todocreatedAt from users u join to_do td on u.userIdx = td.writer join category c on td.categoryIdx = c.categoryIdx  where u.nickname = ?  and TO_CHAR(td.todocreatedAt, 'MM-DD') = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, tododate);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String title = rs.getString("title");
				String status = rs.getString("STATUS");	
				String todocreatedAt = rs.getString("todocreatedAt");
			
				
				result.append(title).append(", ").append(status).append(", ").append(todocreatedAt). append("\n");
				
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
    public static String SelectTodoByUserAndStatus(String userIdx, int statusInt) {
        StringBuilder result = new StringBuilder();
        int loginUser = findByUser(userIdx);
        try {
            String sql = "SELECT * FROM TO_DO WHERE STATUS = ? AND WRITER = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, statusInt);
            pstmt.setInt(2, loginUser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String createdAt = rs.getString("createdAt");
                result.append(title).append(",").append(createdAt).append("\n");
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
    
   

	
	

}

