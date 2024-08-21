package controll;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TodoServer {
    private final TodoThreadPool threadPool;

    public TodoServer(int poolSize) {
        this.threadPool = new TodoThreadPool(poolSize);
    }

    public void startServer() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(7777);
            System.out.println("서버 시작");
            Todo_Controller.connect();
            TodoListController.connect();
            TodoWeeklyGoal.connect();

            while (true) {
                try {
                    // 새로운 클라이언트의 접속을 대기
                    socket = serverSocket.accept();
                    System.out.println("클라이언트 접속");

                    // 스레드 풀에서 ClientHandler 실행
                    threadPool.submitTask(new ClientHandler(socket));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                threadPool.shutdown(); // 서버 종료 시 스레드 풀 종료
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}