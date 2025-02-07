package concurrency.stage0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 자바로 동시에 여러 작업을 처리할 때 스레드를 사용한다.
 * 스레드 객체를 직접 생성하는 방법부터 알아보자.
 * 진행하면서 막히는 부분은 아래 링크를 참고해서 해결한다.
 *
 * Thread Objects
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/threads.html
 *
 * Defining and Starting a Thread
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
 */
class N1_ThreadTest {

    private static final Logger log = LoggerFactory.getLogger(N1_ThreadTest.class);

    /**
     * 자바에서 직접 스레드를 만드는 방법은 2가지가 있다.
     * 먼저 Thread 클래스를 상속해서 스레드로 만드는 방법을 살펴보자.
     * 주석을 참고하여 테스트 코드를 작성하고, 테스트를 실행시켜서 메시지가 잘 나오는지 확인한다.
     */
    @Test
    void testExtendedThread() throws InterruptedException {
        // 하단의 ExtendedThread 클래스를 Thread 클래스로 상속하고 스레드 객체를 생성한다.
        Thread thread = new ExtendedThread("hello thread");

        // 생성한 thread 객체를 시작한다.
         thread.start();

        // thread의 작업이 완료될 때까지 기다린다.
         thread.join();
    }

    /**
     * Runnable 인터페이스를 사용하는 방법도 있다.
     * 주석을 참고하여 테스트 코드를 작성하고, 테스트를 실행시켜서 메시지가 잘 나오는지 확인한다.
     */
    @Test
    void testRunnableThread() throws InterruptedException {
        // 하단의 RunnableThread 클래스를 Runnable 인터페이스의 구현체로 만들고 Thread 클래스를 활용하여 스레드 객체를 생성한다.
        Thread thread = new Thread(new RunnableThread("hello thread"));

        // 생성한 thread 객체를 시작한다.
         thread.start();

        // thread의 작업이 완료될 때까지 기다린다.
         thread.join();
    }

    @DisplayName("Thread 인스턴스 변수 확인")
    @Test
    void test_instance() throws InterruptedException {
        CustomThread thA = new CustomThread("thread-A");
        CustomThread thB = new CustomThread("thread-B");

        thA.run();
        thB.run();

        thA.addVal();
        thA.addVal();

        thB.addVal();

        thA.join();
        thB.join();
    }

    @Test
    void test_0() throws InterruptedException {
        log.info("main flow start");
        final Thread thA = new Thread(()->{
            log.info("th-A start - end");
        }, "th-A");

        final Thread thB = new Thread(()->{
            log.info("th-B start");
            sleep(1000);
            log.info("th-B end");
        }, "th-B");

//        thA.setDaemon(true);
//        thB.setDaemon(true);

        thA.start();
        thB.start();

        thA.join();
        thB.join();

       log.info("main flow end");
        System.out.println();
    }

    private void sleep(final int millis) {
        try {
            log.info("sleep start ! {}", Thread.currentThread().getName());
            Thread.sleep(millis);
            log.info("sleep end ! {}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class CustomThread extends Thread {

        private String message;
        private int val = 0;

        public CustomThread(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info(message);
        }

        public void addVal() {
            val++;
            log.info("val = {}", val);
        }
    }

    private static final class ExtendedThread extends Thread {

        private String message;

        public ExtendedThread(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info(message);
        }
    }

    private static final class RunnableThread implements Runnable {

        private String message;

        public RunnableThread(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info(message);
        }
    }
}
