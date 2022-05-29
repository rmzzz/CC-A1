package app.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Potentially long-running task wrapper.
 *
 * @param <R> Result type
 */
public class Task<R> {
  protected final Task<Object> predecessor;

  protected final Function<Object, R> taskBody;
  protected List<BiFunction<R, Throwable, R>> errorHandlers = new LinkedList<>();

  public <T> Task(Task<T> predecessor, Function<T, R> taskBody) {
    this.predecessor = (Task<Object>) predecessor;
    this.taskBody = (Function<Object, R>) taskBody;
  }

  public <U> Task<U> addStep(Function<R, U> stepBody) {
    return new Task<>(this, stepBody);
  }

  /**
   * add error handler, which can return non-null result if exception is handled successfully
   */
  public Task<R> addErrorHandler(BiFunction<R, Throwable, R> handler) {
    errorHandlers.add(handler);
    return this;
  }

  public CompletionStage<R> execute() {
    CompletionStage<?> predecessorStage = predecessor.execute();
    CompletionStage<R> stage = predecessorStage.thenApply(taskBody);
    if (!errorHandlers.isEmpty()) {
      stage = stage.handle(this::triggerErrorHandlers);
    }
    return stage;
  }

  protected R triggerErrorHandlers(R result, Throwable throwable) {
    if (throwable != null) {
      Throwable error = throwable;
      if (error instanceof CompletionException completionException) {
        error = completionException.getCause();
      }
      if (error instanceof ExecutionException executionException) {
        error = executionException.getCause();
      }
      for (var errorHandler : errorHandlers) {
        R handlerResult = errorHandler.apply(result, error);
        if (handlerResult != null) {
          return handlerResult;
        }
      }
    }
    return result;
  }
}
