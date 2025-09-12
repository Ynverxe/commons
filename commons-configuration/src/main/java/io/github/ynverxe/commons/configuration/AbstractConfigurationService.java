package io.github.ynverxe.commons.configuration;

import io.github.ynverxe.commons.service.AbstractService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public abstract class AbstractConfigurationService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurationService.class);

  private final Map<String, LazyNode> configurationMap = new ConcurrentHashMap<>();

  public AbstractConfigurationService() {
    registerConfiguration("main", "config.yml", "config.yml");
  }

  @Override
  public void performStart() throws Exception {
    this.configurationMap.forEach((name, confguration) -> {
      ConfigurationNode node = confguration.load();
      if (confguration == null) {
        this.configurationMap.remove(name);
      }
    });
  }

  @Override
  public void performReload() throws Exception {
    for (LazyNode value : this.configurationMap.values()) {
      value.node = null;

      value.load();
    }
  }

  @Override
  protected void performStop() throws Exception {}

  public @NotNull Optional<LazyNode> get(@NotNull String name) {
    return Optional.ofNullable(this.configurationMap.get(name));
  }

  public @NotNull LazyNode getOrThrow(@NotNull String name) {
    return get(name).orElseThrow(() -> new IllegalStateException("Missing node '" + name + "'"));
  }

  public @NotNull ConfigurationNode mainConfiguration() {
    return get("main").map(lazyNode -> lazyNode.node).orElseThrow(() -> {
      return new IllegalStateException("Main configuration is missing");
    });
  }

  public @UnmodifiableView Map<String, LazyNode> configurationMap() {
    return Collections.unmodifiableMap(configurationMap);
  }

  protected void registerConfiguration(String name, String fallbackFile, String destinationFile) {
    this.configurationMap.put(Objects.requireNonNull(name, "name"), new LazyNode(name, () -> this.createNode(fallbackFile, destinationFile)));
  }

  protected abstract ConfigurationNode createNode(String fallbackFile, String destinationFile) throws IOException;

  public final class LazyNode {
    private final String name;
    private final Callable<ConfigurationNode> configurationSupplier;
    private @Nullable ConfigurationNode node;

    public LazyNode(String name, Callable<ConfigurationNode> configurationSupplier) {
      this.name = name;
      this.configurationSupplier = Objects.requireNonNull(configurationSupplier);
    }

    public ConfigurationNode load() {
      if (this.node == null) {
        try {
          this.node = this.configurationSupplier.call();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      return this.node;
    }
  }
}