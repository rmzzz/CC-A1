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
  /**
   * Execute all tasks from the queue, then merge result using given merge function.
   *
   * @param tasksQueue    The tasks queue.
   * @param defaultResult An empty result instance.
   * @param resultMerger  The result merge function.
   * @param <R>           The task result type.
   * @return The merged result.
   * @implSpec the tasks queue may be updated during task execution.
   *   That is, an implementation must guarantee that all task results are merged.
   */
  default <R> R executeAllTasksThenMergeResult(Queue<Task<R>> tasksQueue,
                                               R defaultResult,
                                               BinaryOperator<R> resultMerger) {
    List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
    AtomicReference<R> resultHolder = new AtomicReference<>(defaultResult);
    do {
      // caveat: do not replace the loop by iterator nor stream, because tasksQueue can be modified during processing!
      for (Task<R> task = tasksQueue.poll(); task != null; task = tasksQueue.poll()) {
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
    } while (completableFutures.stream().anyMatch(cs -> !cs.isDone()));
    return resultHolder.get();
  }

  /**
   * Execute the provided task.
   * Depending on executor the task may be executed as synchronously as asynchronously.
   * By default, the {@link Task#execute()}  is called.
   *
   * @param task The instance of task to execute.
   * @param <R>  The task result type, i.e. task output
   * @return A completion stage holding the future result of the task execution.
   */
  default <R> CompletionStage<R> executeTask(Task<R> task) {
    return task.execute();
  }

  /**
   * create an instance of Task to be executed in this TaskExecutor.
   *
   * @param parameter the Task parameter
   * @param taskBody  the function that implements the actual task
   * @param <T>       The task parameter type, i.e. task input
   * @param <R>       The task body return type, i.e. task output
   * @return The instance of Task wrapping the taskBody
   */
  <T, R> Task<R> createTask(T parameter, Function<T, R> taskBody);
}
