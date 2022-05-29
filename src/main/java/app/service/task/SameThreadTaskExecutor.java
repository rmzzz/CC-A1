package app.service.task;

import app.domain.Task;
import app.domain.TaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class SameThreadTaskExecutor implements TaskExecutor {
  @Override
  public <T, R> Task<R> createTask(T parameter, Function<T, R> taskBody) {
    SameThreadTask<T> root = new SameThreadTask<>(parameter);
    Task<R> task = new Task<>(root, taskBody);
    return task;
  }

  static class SameThreadTask<T> extends Task<T> {
    protected SameThreadTask(T parameter) {
      super(null, arg -> parameter);
    }

    @Override
    public CompletionStage<T> execute() {
      return CompletableFuture.completedFuture(taskBody.apply(null));
    }
  }
}
