package controll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TodoServer {
	public static void server() {
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(7777);
			System.out.println("서버 시작");
			Todo_Controller.connect();
			TodoListController.connect();
			TodoWeeklyGoal.connect();
//			while (true) {
				try {
					//서버가 새로운 클라이언트가 접속할 때마다 ClientHandler생성
					while(true) {
					socket = serverSocket.accept();
					System.out.println("클라이언트 접속");
					new ClientHandler(socket).start();;
					}
					// 클라이언트와의 통신 로직 추가 (예: 메시지 수신 및 전송)

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// 리소스 정리 (필요에 따라)
					try {
						if (socket != null)
							socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
//			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 서버 소켓 종료
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
