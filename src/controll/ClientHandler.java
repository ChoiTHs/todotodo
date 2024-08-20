package controll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	public ClientHandler(Socket socket) {
		this.socket = socket;
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				String response = null;
				try {
					int choice = in.readInt();
					System.out.println(choice + "번 요청 처리 중");
					switch (choice) {
					case 1:
						while (true) {
							String name = in.readUTF();
							String pwd = in.readUTF();
							response = Todo_Controller.insert(name, pwd);
							out.writeUTF(response);
							out.flush();
							if (response.startsWith("환영합니다")) {
								break;
							}
						}
						break;

					case 2:
						while (true) {
							String name = in.readUTF();
							String pwd = in.readUTF();
							response = Todo_Controller.login(name, Integer.parseInt(pwd));
							out.writeUTF(response);
							out.flush();
							if (response.equals("로그인 성공")) {
								break;
							}
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
					case 4:
						System.out.println("카테고리 조회");
						String name = in.readUTF();
						String category = in.readUTF();
						response = TodoListController.selectByCotegory(name, category);
						out.writeUTF(response);
						out.flush();
						break;

					case 7: {
						System.out.println("할 일 달성 여부 변경");
						String userIdx = in.readUTF();
						int status = in.readInt();
						String todoTblIdx = in.readUTF();
						response = TodoListController.updateStatus(status, todoTblIdx, userIdx);
						out.writeUTF(response);
						out.flush();
						break;
					}
					case 8: {
						System.out.println("할 일 변경");
						String userIdx = in.readUTF();
						String todoTblIdx = in.readUTF();
						String title = in.readUTF();

						response = TodoListController.updateTitle(title, todoTblIdx, userIdx);
						out.writeUTF(response);
						out.flush();
						break;
					}

					case 9: {
						System.out.println("사용자의 todo 일별 조회");
						String names = in.readUTF();
						String todoDate = in.readUTF();
						response = TodoListController.selectByUserDateTodo(names, todoDate);
						out.writeUTF(response);
						out.flush();
						break;
					}
					case 10: {
						System.out.println("주간목표 생성 요청");

						String userIdx = in.readUTF();
						String todoContent = in.readUTF();

						response = TodoWeeklyGoal.insert(userIdx, todoContent);
						out.writeUTF(response);
						out.flush();

						System.out.println("주간목표 생성 응답 완료");
						break;
					}
					case 11: {
						System.out.println("사용자의 주간목표 조회");
						String week_name = in.readUTF();
						String week_date = in.readUTF();
						response = TodoWeeklyGoal.SelectweeklyGoal(week_name, week_date);
						out.writeUTF(response);
						out.flush();
						break;
					}
					case 12: {
						int statusInt = in.readInt();
						String userIdx = in.readUTF();
						if (statusInt == 1) {
							System.out.println("완료한 할 일 조회");
							response = TodoListController.SelectTodoByUserAndStatus(userIdx, statusInt);
							out.writeUTF(response);
							out.flush();
							break;
						} else {
							System.out.println("완료하지 않은 할 일 조회");
							response = TodoListController.SelectTodoByUserAndStatus(userIdx, statusInt);
							out.writeUTF(response);
							out.flush();
							break;
						}
					}
					case 13:
						System.out.println("시스템 종료");
						String userName = in.readUTF();
						try {
							if(socket != null && !socket.isClosed()) {
								response = Todo_Controller.logout(userName);
								out.writeUTF(response);
								socket.close();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} catch (IOException e) {
					System.out.println("클라이언트 연결 종료");
					break; // 클라이언트 연결 종료 시 루프를 빠져나감
				}
			}
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
