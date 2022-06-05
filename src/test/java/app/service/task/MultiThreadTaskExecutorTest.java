package app.service.task;

import app.domain.Task;
import app.domain.TaskExecutor;
import app.tests.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class MultiThreadTaskExecutorTest extends BaseUnitTest {

  /** min number of threads to test on more the cores */
  public static final int NUMBER_OF_TEST_THREADS = 32;
  public static final int TEST_SLEEP_TIME = 256;

  ExecutorService executorService;

  TaskExecutor executor;

  Queue<Task<String>> queue;

  TaskExecutor createExecutor() {
    return new MultiThreadTaskExecutor(executorService);
  }

  @BeforeEach
  void setUp() {
    queue = new ConcurrentLinkedQueue<>();
    executorService = Executors.newFixedThreadPool(NUMBER_OF_TEST_THREADS);
    executor = createExecutor();
  }

  @Test
  void createTaskShouldCreateMultiThreadTask() {
    Task<String> task = executor.createTask("test", p -> "executed");
    Task<?> root = task;
    while (root.getPredecessor() != null) {
      root = root.getPredecessor();
    }
    assertTrue(root instanceof MultiThreadTaskExecutor.MultiThreadTask);
  }

  @Test
  void executeTaskShouldBeConcurrent() {
    Task<String>[] tasks = new Task[NUMBER_OF_TEST_THREADS*2];
    for(int i = 0; i < tasks.length; i++) {
      tasks[i] = executor.createTask(i, this::concurrentTestTaskBody);
    }
    CompletableFuture<String>[] futures = new CompletableFuture[tasks.length];
    long startTime = System.currentTimeMillis();
    for(int i = 0; i < tasks.length; i++) {
      futures[i] = executor.executeTask(tasks[i]).toCompletableFuture();
    }
    // at least one is not yet ready
    assertTrue(Arrays.stream(futures).anyMatch(f -> !f.isDone()));
    // wait for all tasks completion
    CompletableFuture.allOf(futures).join();
    long endTime = System.currentTimeMillis();
    long testTime = endTime - startTime;
    // all tasks are executed at most double as fast comparing to sequential execution (due to concurrency)!
    assertTrue(testTime < TEST_SLEEP_TIME * NUMBER_OF_TEST_THREADS,
            () -> "Test time " + testTime + " exceeds double time " + TEST_SLEEP_TIME*NUMBER_OF_TEST_THREADS);
  }

  String concurrentTestTaskBody(int taskNumber) {
      try {
        Thread.sleep(TEST_SLEEP_TIME);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return "executed#" + taskNumber;
  }

  @Test
  void executeAllTasksThenMergeResultShouldGuaranteeThatAllTasksAreMerged() {
    for (int i = 1; i <= NUMBER_OF_TEST_THREADS; i++) {
      Task<String> taskMock = executor.createTask(i * 10, this::testTaskBody);
      queue.add(taskMock);
    }
    String result = executor.executeAllTasksThenMergeResult(queue, "", String::concat);
    assertEquals(NUMBER_OF_TEST_THREADS * 3, result.length());
  }

  String testTaskBody(int parameter) {
    if ((parameter % 10) < 2) {
      Task<String> subTask = executor.createTask(parameter + 1, this::testTaskBody);
      logger.debug("task({}) spawned {}", parameter, subTask);
      queue.add(subTask);
    }
    String result = Character.toString('a' + parameter);
    logger.debug("task executed ({}) => {}", parameter, result);
    return result;
  }

  @Test
  void executeTask() throws Exception {
    Task<String> task = executor.createTask("test", p -> "mock");
    CompletionStage<String> result = executor.executeTask(task);
    assertEquals("mock", result.toCompletableFuture().get());
  }
}