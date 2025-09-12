package io.github.ynverxe.commons.service;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractService implements Service {

  private final Map<Class<? extends Service>, Service> services = new LinkedHashMap<>();

  @Override
  public final void start() throws Exception {
    performStart();

    for (Service service : this.services.values()) {
      executeService(service, Service::start, Operation.START);
    }
  }

  @Override
  public final void reload() throws Exception {
    performReload();

    for (Service service : this.services.values()) {
      executeService(service, Service::reload, Operation.RELOAD);
    }
  }

  @Override
  public final void stop() throws Exception {
    performStop();

    for (Service service : this.services.values()) {
      executeService(service, Service::stop, Operation.STOP);
    }
  }

  protected abstract void performStart() throws Exception;

  protected abstract void performReload() throws Exception;

  protected abstract void performStop() throws Exception;

  protected final void registerChild(@NotNull Service service) {
    if (this.services.containsKey(service.getClass())) {
      throw new IllegalArgumentException("Service '" + service.getClass() + "' is already registered");
    }

    this.services.put(service.getClass(), service);
  }

  public interface ServiceConsumer {
    void consume(Service service) throws Exception;
  }

  public static void executeService(Service service, ServiceConsumer consumer, Operation operation) {
    try {
      consumer.consume(service);
    } catch (Exception e) {
      throw new io.github.ynverxe.commons.service.exception.ServiceExecutionException("Unexpected exception while executing service at phase '" + operation.name() + "'", e);
    }
  }

  public enum Operation {
    START,
    RELOAD,
    STOP
  }
}