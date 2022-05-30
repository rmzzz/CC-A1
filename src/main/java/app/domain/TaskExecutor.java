package app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface TaskExecutor {
  default <T, R> R executeAllTasksThenMergeResult(Queue<Task<R>> tasksQueue,
                                          R defaultResult,
                                          BinaryOperator<R> resultMerger) {
    List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
    AtomicReference<R> resultHolder = new AtomicReference<>(defaultResult);
    do {
      // caveat: do not replace the loop by iterator nor stream, because tasksQueue can be modified during processing!
      for(Task<R> task = tasksQueue.poll(); task != null; task = tasksQueue.poll()) {
        var future = executeTask(task)
                .thenAccept(nextResult -> {
                  // synchronize on resultHolder in order to ensure all reports are merged
                  synchronized (resultHolder) {
                    R result = resultMerger.apply(resultHolder.get(), nextResult);
                    resultHolder.set(result);
                  }
                })
                .toCompletableFuture();
        completableFutures.add(future);
      }
//      try {
//        Thread.sleep(100L);
//      } catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//        break;
//      }
    } while (completableFutures.stream().anyMatch(cs -> !cs.isDone()));
    return resultHolder.get();
  }

  default <R> CompletionStage<R> executeTask(Task<R> task) {
      return task.execute();
  }

  <T, R> Task<R> createTask(T parameter, Function<T, R> taskBody);
}
