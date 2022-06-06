package app.service.task;

import app.domain.InputParameters;
import app.domain.Task;
import app.domain.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class MultiThreadTaskExecutor implements TaskExecutor {
  static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadTaskExecutor.class);

  final ExecutorService executor;

  protected MultiThreadTaskExecutor(ExecutorService executor) {
    this.executor = executor;
  }

  public MultiThreadTaskExecutor(InputParameters parameters) {
    this(parameters.getThreadsCount() == 0
            ? Executors.newWorkStealingPool()
            : Executors.newWorkStealingPool(parameters.getThreadsCount()));
  }

  @Override
  public <T, R> Task<R> createTask(T parameter, Function<T, R> taskBody) {
    MultiThreadTask<T> root = new MultiThreadTask<>(parameter);
    Task<R> task = new Task<>(root, taskBody);
    return task;
  }

  @Override
  public <R> R executeAllTasksThenMergeResult(Queue<Task<R>> tasksQueue,
                                              R defaultResult,
                                              BinaryOperator<R> resultMerger) {
    List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
    AtomicReference<R> resultHolder = new AtomicReference<>(defaultResult);
    do {
      // caveat: do not replace the loop by iterator nor stream, because tasksQueue can be modified during processing!
      for (Task<R> task = tasksQueue.poll(); task != null; task = tasksQueue.poll()) {
        LOGGER.debug("polling {}", task);
        var future = executeTask(task)
                .thenAccept(nextResult -> {
                  // synchronize on resultHolder in order to ensure all reports are merged
                  synchronized (resultHolder) {
                    R result = resultMerger.apply(resultHolder.get(), nextResult);
                    resultHolder.set(result);
                    LOGGER.debug("merged result: {}", result);
                  }
                })
                .toCompletableFuture();
        completableFutures.add(future);
      }

    } while (completableFutures.stream().anyMatch(cs -> !cs.isDone()));
    R result;
    synchronized (resultHolder) {
      result = resultHolder.get();
    }
    LOGGER.debug("{} tasks done; result: {}", completableFutures.size(), result);
    return result;
  }

  class MultiThreadTask<T> extends Task<T> {
    T parameter;

    protected MultiThreadTask(T parameter) {
      super(null, Function.identity());
      this.parameter = parameter;
    }

    @Override
    public CompletionStage<T> execute() {
      return CompletableFuture.supplyAsync(this::wrapAsyncExecution, executor);
    }

    T wrapAsyncExecution() {
      LOGGER.debug("executing {}", this);
      return taskBody.apply(parameter);
    }

    @Override
    public String toString() {
      return "MultiThreadTask(" + parameter + ")";
    }
  }
}
