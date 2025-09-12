package io.github.ynverxe.commons.ticking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class EntityTicker<T extends Tickable> {

  private final Map<String, T> entities = new ConcurrentHashMap<>();
  private final @NotNull ErrorStrategy errorStrategy;
  private final @NotNull Logger logger;
  private final @Nullable BiConsumer<String, T> removeListener;

  public EntityTicker(@NotNull ErrorStrategy errorStrategy, @NotNull Logger logger, @Nullable BiConsumer<String, T> removeListener) {
    this.errorStrategy = Objects.requireNonNull(errorStrategy);
    this.logger = Objects.requireNonNull(logger);
    this.removeListener = removeListener;
  }

  public EntityTicker(@NotNull ErrorStrategy errorStrategy, @Nullable BiConsumer<String, T> removeListener) {
    this(errorStrategy, LoggerFactory.getLogger("EntityTicker"), removeListener);
  }

  public void tickEntities() {
    Iterator<Map.Entry<String, T>> iterator = this.entities.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, T> entry = iterator.next();
      T entity = entry.getValue();

      try {
        entity.tick();
      } catch (Throwable throwable) {
        this.logger.error("Unexpected error while ticking entity '{}'", entry.getKey(), throwable);

        if (this.errorStrategy != ErrorStrategy.DISCARD) {
          continue;
        }

        iterator.remove();

        if (this.removeListener != null) {
          this.removeListener.accept(entry.getKey(), entity);
        }
      }
    }
  }

  public @UnmodifiableView Map<String, T> entitiesView() {
    return Collections.unmodifiableMap(this.entities);
  }

  public void register(@NotNull String key, @NotNull T entity) {
    this.entities.put(key, entity);
  }

  public boolean unregister(@NotNull String key) {
    return this.entities.remove(key) != null;
  }

  public enum ErrorStrategy {
    DISCARD,
    CONTINUE;
  }
}