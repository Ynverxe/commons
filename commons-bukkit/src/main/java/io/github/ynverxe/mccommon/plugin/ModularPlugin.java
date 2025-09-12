package io.github.ynverxe.mccommon.plugin;

import io.github.ynverxe.commons.service.Service;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.ynverxe.commons.service.AbstractService.*;

public class ModularPlugin extends JavaPlugin {

  private final Map<Class<?>, Service> services = new LinkedHashMap<>();

  @Override
  public void onEnable() {
    for (Service service : this.services.values()) {
      // Don't let the plugin enable if there's an exception
      executeService(service, Service::start, Operation.START);
    }
  }

  public void reload() {
    for (Service service : this.services.values()) {
      try {
        executeService(service, Service::reload, Operation.RELOAD);
      } catch (Exception e) {
        getSLF4JLogger().error("Unexpected error while reloading service", e);
      }
    }
  }

  @Override
  public void onDisable() {
    for (Service service : this.services.values()) {
      try {
        executeService(service, Service::stop, Operation.STOP);
      } catch (Exception e) {
        getSLF4JLogger().error("Unexpected error while stopping service", e);
      }
    }
  }

  protected final void addService(@NotNull Service service) {
    if (isEnabled())
      throw new IllegalStateException("Cannot add modules once plugin is enabled");

    this.services.put(service.getClass(), service);
  }

  public <T extends Service> @NotNull Optional<T> service(@NotNull Class<T> serviceClass) {
    Service found = this.services.get(serviceClass);
    return Optional.ofNullable(serviceClass.cast(found));
  }

  public <T extends Service> @NotNull T getService(@NotNull Class<T> serviceClass) {
    return service(serviceClass).orElseThrow(() -> new IllegalStateException("Cannot found service of type: " + serviceClass));
  }
}