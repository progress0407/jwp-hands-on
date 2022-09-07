package concurrency.stage0;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까? 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 * <p>
 * Thread Pools https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 * <p>
 * Introduction to Thread Pools in Java https://www.baeldung.com/thread-pool-java-and-guava
 */
class N3_ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(N3_ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() {

        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1; // 2개 실행, 한 개 대기!

        assertAll(
                () -> assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize),
                () -> assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize)
        );
    }

    @Test
    void testNewCachedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;

        assertAll(
                () -> assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize),
                () -> assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize)
        );
    }

    /**
     * https://leeyh0216.github.io/posts/truth_of_threadpoolexecutor/
     */
    @DisplayName("블로그 글을 보고 테스트 발췌")
    @Test
    void blog() throws InterruptedException {
        int numTasks = 60;
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(5);
        CountDownLatch countDownLatch = new CountDownLatch(numTasks);
        ThreadPoolExecutor threadPoolExecutor= new ThreadPoolExecutor(10, 50, 10,
                TimeUnit.SECONDS, blockingQueue);

        for(int i = 0; i < numTasks; i++){
            threadPoolExecutor.submit(() -> {
                try {
                    Thread.sleep(1000);
                }
                catch(Exception e){
                }
                countDownLatch.countDown();
            });
        }

        for(int i = 0; i < 120; i++){
            Thread.sleep(500);
            //현재 실행 중인 Thread의 수 출력
            System.out.println("Active: " + threadPoolExecutor.getActiveCount());
            //Queue에서 대기 중인 작업 갯수 출력
            System.out.println("Queue: " + blockingQueue.size());
        }

        threadPoolExecutor.shutdown();
    }

    private Runnable logWithSleep(final String message) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
