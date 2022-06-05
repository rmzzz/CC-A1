package app.service.task;

import app.domain.Task;
import app.domain.TaskExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TaskExecutorTest {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  protected TaskExecutor executor;

  protected Queue<Task<String>> queue;

  @BeforeEach
  void setUp() {
    queue = new ConcurrentLinkedQueue<>();
    executor = createExecutor();
  }

  protected TaskExecutor createExecutor() {
    return new TaskExecutor() {
      @Override
      public <T, R> Task<R> createTask(T parameter, Function<T, R> body) {
        Task<T> root = new Task<T>(null, arg -> parameter) {
          @Override
          public CompletionStage<T> execute() {
            return CompletableFuture.completedFuture(taskBody.apply(null));
          }
        };
        return new Task<>(root, body);
      }
    };
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
  void executeAllTasksThenMergeResult() {
    Task<String> taskMock =  executor.createTask(0, this::testTaskBody);
    queue.add(taskMock);
    String result = executor.executeAllTasksThenMergeResult(queue, "", String::concat);
    assertEquals("abc", result);
  }

  @Test
  void executeTask() throws Exception {
    Task<String> taskMock = executor.createTask("test", p -> "mock");
    CompletionStage<String> result = executor.executeTask(taskMock);
    assertEquals("mock", result.toCompletableFuture().get());
  }
}