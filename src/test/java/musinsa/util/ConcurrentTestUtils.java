package musinsa.util;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ConcurrentTestUtils {

  private static final int TIMEOUT_SECONDS = 10;

  /**
   * 스레드 수 만큼 task를 동시 실행하고 모든 작업이 완료될 때까지 기다림
   */
  public static void concurrentTask(int threadCount, Runnable task) throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executor.execute(() -> {
        try {
          task.run();
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    executor.shutdown();

    if (!completed) {
      throw new RuntimeException("모든 스레드가 설정한 시간내에 완료되지 않았습니다.");
    }
  }

  /**
   * 스레드 수 만큼 task를 동시 실행하고 모든 결과를 모아서 반환
   */
  public static <T> List<T> concurrentTaskWithResult(int threadCount, Supplier<T> supplier) throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    List<T> resultList = new CopyOnWriteArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          T result = supplier.get();
          resultList.add(result);
        } catch (Exception e) {
          System.out.println("예외 발생: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    executor.shutdown();

    if (!completed) {
      throw new RuntimeException("모든 스레드가 설정한 시간내에 완료되지 않았습니다.");
    }

    return resultList;
  }
}

