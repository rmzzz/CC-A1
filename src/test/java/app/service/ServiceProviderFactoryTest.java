package app.service;

import app.domain.InputParameters;
import app.domain.ServiceProvider;
import app.service.task.MultiThreadTaskExecutor;
import app.service.task.SingleThreadTaskExecutor;
import app.tests.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceProviderFactoryTest extends BaseUnitTest {

  InputParameters parameters;

  @BeforeEach
  void setUp() {
    parameters = mock(InputParameters.class);
  }

  @Test
  void createMultiThreadedByDefault() {
    ServiceProvider provider = ServiceProviderFactory.create(parameters);
    assertTrue(provider.getTaskExecutor() instanceof MultiThreadTaskExecutor);
  }

  @Test
  void createSingleThreadedForThreads1() {
    doReturn(1).when(parameters).getThreadsCount();
    ServiceProvider provider = ServiceProviderFactory.create(parameters);
    assertTrue(provider.getTaskExecutor() instanceof SingleThreadTaskExecutor);
  }

  @Test
  void createMultiThreadedForThreads2() {
    doReturn(2).when(parameters).getThreadsCount();
    ServiceProvider provider = ServiceProviderFactory.create(parameters);
    assertTrue(provider.getTaskExecutor() instanceof MultiThreadTaskExecutor);
  }

}