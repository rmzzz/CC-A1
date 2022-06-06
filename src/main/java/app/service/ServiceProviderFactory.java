package app.service;

import app.domain.InputParameters;
import app.domain.ServiceProvider;
import app.service.provider.MultiThreadServiceProvider;
import app.service.provider.SingleThreadServiceProvider;

public class ServiceProviderFactory {
  public static ServiceProvider create(InputParameters parameters) {
    if (parameters.getThreadsCount() == 1) {
      return new SingleThreadServiceProvider(parameters);
    }
    return new MultiThreadServiceProvider(parameters);
  }
}
