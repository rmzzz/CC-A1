package app.service.task;

import app.domain.Task;
import app.domain.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class SingleThreadTaskExecutor implements TaskExecutor {
  @Override
  public <T, R> Task<R> createTask(T parameter, Function<T, R> taskBody) {
    SingleThreadTask<T> root = new SingleThreadTask<>(parameter);
    Task<R> task = new Task<>(root, taskBody);
    return task;
  }

  public <R> R executeAllTasksThenMergeResult(Queue<Task<R>> tasksQueue,
                                 R defaultResult,
                                 BinaryOperator<R> resultMerger) {
    List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
    AtomicReference<R> resultHolder = new AtomicReference<>(defaultResult);
    do {
      // caveat: do not replace the loop by iterator nor stream, because tasksQueue can be modified during processing!
      for (Task<R> task = tasksQueue.poll(); task != null; task = tasksQueue.poll()) {
        var future = executeTask(task)
                .thenAccept(nextResult -> {
                  R result = resultMerger.apply(resultHolder.get(), nextResult);
                  resultHolder.set(result);
                })
                .toCompletableFuture();
        completableFutures.add(future);
      }
    } while (completableFutures.stream().anyMatch(cs -> !cs.isDone()));
    return resultHolder.get();
  }

  static class SingleThreadTask<T> extends Task<T> {
    protected SingleThreadTask(T parameter) {
      super(null, arg -> parameter);
    }

    @Override
    public CompletionStage<T> execute() {
      return CompletableFuture.completedFuture(taskBody.apply(null));
    }
  }
}
