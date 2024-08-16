package controll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class ClientHandler extends Thread {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private String users;

	public ClientHandler(Socket socket) {
		// 서버에서 만든 소캣을 핸들러에 저장
		this.socket = socket;
		try {
			// 입/출력 선언
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 스레드 시작
	@Override
	public void run() {
		try {
			while (true) {
				String response = null;
				try {
					int choice = in.readInt();
					System.out.println(choice + "번 넘어왔음");
					switch (choice) {
					case 1:{
						String name = in.readUTF();
						String pwd = in.readUTF();
						System.out.println(pwd);
						response = Todo_Controller.insert(name, pwd);
						out.writeUTF(response);
						out.flush();

						break;
					}
					case 2:
						String name = in.readUTF();
						String pwd = in.readUTF();

						// 로그인 검증 로직
						response = Todo_Controller.login(name, pwd);
						out.writeUTF(response);
						out.flush();

						// 로그인 성공 시, 현재 사용자 정보를 저장하거나 상태를 업데이트할 수 있습니다.
						if (response.equals("로그인 성공")) {
							// 추가적인 로그인 성공 처리를 여기에 추가할 수 있습니다.
						}
						break;
					case 3: {
						System.out.println("테이블 조회 요청");
						response = Todo_Controller.selectAll();
						out.writeUTF(response);

						out.flush();
						System.out.println();
						System.out.println("테이블 조회 응답 완료");
						break;
					}
					case 4: {
						System.out.println("카테고리 조회");
						String name_cg = in.readUTF();
						String category = in.readUTF();

						response = TodoListController.selectByCotegory(name_cg, category);
						System.out.println("server " + response);
						out.writeUTF(response);
						out.flush();
						break;
					}
					case 5: {
						System.out.println("오늘의 할 일 요청");
						String userIdx = in.readUTF();

						String title = in.readUTF();
						int categoryIdx = in.readInt();
						String date = in.readUTF();
						String importance = in.readUTF();

						// 데이터 처리 및 응답
						response = TodoListController.insert(userIdx, title, categoryIdx, date, importance);
						out.writeUTF(response);
						out.flush();
						break;
					}
					case 6: {
						System.out.println("오늘의 할 일 삭제");
						String userIdx = in.readUTF();
						String todo_title = in.readUTF();

						response = TodoListController.delete(todo_title, userIdx);
						out.writeUTF(response);
						out.flush();
						System.out.println("할 일 입력 응답 완료");
						break;
					}

					case 7: {
						System.out.println("할 일 달성 여부 변경");
						String todoTitle = in.readUTF();
						String userIdx = in.readUTF();
						response = TodoListController.updateStatus(todoTitle, userIdx);
						out.writeUTF(response);
						out.flush();
						break;
					}
					case 8:
						System.out.println("할 일 변경");
						String userIdx_1 = in.readUTF();
						String title = in.readUTF();
						String newTitle = in.readUTF();

						response = TodoListController.updateTitle(userIdx_1, title, newTitle);
						out.writeUTF(response);
						out.flush();
						break;

					case 9:
						System.out.println("사용자의 todo 일별 조회");
						String names = in.readUTF();
						String todoDate = in.readUTF();

						response = TodoListController.selectByUserDateTodo(names, todoDate);

						out.writeUTF(response);
						out.flush();
						break;

					case 10:
						System.out.println("주간목표 생성 요청");

						String userIdx = in.readUTF();
						String todoContent = in.readUTF();

						response = TodoWeeklyGoal.insert(userIdx, todoContent);
						out.writeUTF(response);
						out.flush();

						System.out.println("주간목표 생성 응답 완료");
						break;

					case 11:
						System.out.println("사용자의 주간목표 조회");
						String week_name = in.readUTF();
						String week_date = in.readUTF();

						// 주간 목표 조회 및 달성률 계산
						StringBuilder weeklyresponse = new StringBuilder();
						String achievementRate = TodoWeeklyGoal.SelectweeklyGoal(week_name, week_date, weeklyresponse);

						// 결과를 클라이언트에 전송
						out.writeUTF(weeklyresponse.toString()); // 주간 목표 데이터
						out.writeUTF(achievementRate); // 달성률
						out.flush();
						break;

					case 12: {
						System.out.println("ddd");
						String statususerIdx = in.readUTF(); // loginUser를 먼저 읽기
						String statusInt = in.readUTF(); // 그다음 status 읽기

						if (statusInt.startsWith("1")) {
							System.out.println("완료한 할 일 조회");
							response = TodoListController.SelectTodoByUserAndStatus(statususerIdx, statusInt);
						} else {
							System.out.println("완료하지 않은 할 일 조회");
							response = TodoListController.SelectTodoByUserAndStatus(statususerIdx, statusInt);
						}
						out.writeUTF(response);
						out.flush();
						System.out.println(response + "서버");
						break;
					}

					case 13:
						System.out.println("시스템 종료");
						try {
							// 유저의 상태 값 0으로 변경 후 소켓 닫음
//    							Todo_Controller.logout(users);
							String useridx = in.readUTF();
							response = Todo_Controller.logout(useridx);
							out.writeUTF(response);
							out.flush();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case 14:
						System.out.println("투두 전체조회");
						String selectuserIdx = in.readUTF();
						response = TodoListController.selectAllTodo(selectuserIdx);
						out.writeUTF(response);

						out.flush();
						System.out.println(response);
						break;

					case 15:
						System.out.println("주간 전체조회");
						String weeklyIdx = in.readUTF();
						response = TodoWeeklyGoal.selecAlltWeelyGoal(weeklyIdx);
						out.writeUTF(response);

						out.flush();
						System.out.println(response);
						break;

					case 17:
						System.out.println("유저의 할 일 전체 조회");
						String user = in.readUTF();
						response = TodoListController.selectAllTodo(user);
						out.writeUTF(response);

						out.flush();
						System.out.println(response);
						break;

					case 18:
						System.out.println("유저의 주간목표 완료 요청");
						String weeklyTitle = in.readUTF();
						String user_idx = in.readUTF();
						System.out.println(weeklyTitle);
						System.out.println(user_idx);
						response = TodoWeeklyGoal.weeklyUpdate(weeklyTitle, user_idx);
						out.writeUTF(response);
						out.flush();
						System.out.println("주간 목표 수정 응답 완료");
						break;

					case 19:
						System.out.println("사용자의 주간목표 전체 조회 요청");
						String user_i = in.readUTF();
						response = TodoWeeklyGoal.selectAllWeeklyByUser(user_i);
						out.writeUTF(response);
						out.flush();
						System.out.println("사용자의 주간목표 전체 조회 응답 완료");
						break;
					}

				} catch (Exception e) {
					System.out.println(users + "클라이언트 연결 끊어짐");
					Todo_Controller.logout(users);
					e.printStackTrace();
					break;
				}
			}
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
