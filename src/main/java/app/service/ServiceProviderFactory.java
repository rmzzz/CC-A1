package app.service;

import app.domain.InputParameters;
import app.domain.ServiceProvider;
import app.service.provider.MultiThreadServiceProvider;
import app.service.provider.SingleThreadServiceProvider;

public class ServiceProviderFactory {
  public static ServiceProvider create(InputParameters parameters) {
    if (parameters.getUrls().size() > 1) {
      return new MultiThreadServiceProvider(parameters);
    }
    return new SingleThreadServiceProvider(parameters);
  }
}
