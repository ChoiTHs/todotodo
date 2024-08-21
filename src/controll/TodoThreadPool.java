package controll;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoThreadPool {
    private final ExecutorService executorService;

    // 생성자에서 스레드 풀 크기를 설정
    public TodoThreadPool(int poolSize) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    // 작업을 스레드 풀에서 실행
    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    // 서버 종료 시 스레드 풀 종료
    public void shutdown() {
        executorService.shutdown();
    }
}
