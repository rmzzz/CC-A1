package app.service.task;

import app.domain.Task;
import app.domain.TaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class MultiThreadTaskExecutor implements TaskExecutor {
  final ExecutorService executor;

  public MultiThreadTaskExecutor() {
    executor = Executors.newWorkStealingPool();
  }

  @Override
  public <T, R> Task<R> createTask(T parameter, Function<T, R> taskBody) {
    MultiThreadTask<T> root = new MultiThreadTask<>(parameter);
    Task<R> task = new Task<>(root, taskBody);
    return task;
  }

  class MultiThreadTask<T> extends Task<T> {
    protected MultiThreadTask(T parameter) {
      super(null, arg -> parameter);
    }

    @Override
    public CompletionStage<T> execute() {
      return CompletableFuture.supplyAsync(() -> taskBody.apply(null), executor);
    }
  }
}
