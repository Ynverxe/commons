package io.github.ynverxe.commons.configuration;

import io.github.ynverxe.configuratehelper.handler.factory.ConfigurationLoaderFactory;
import io.github.ynverxe.configuratehelper.handler.source.URLConfigurationFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.UnaryOperator;

@SuppressWarnings("all")
public class DefaultConfigurationService extends AbstractConfigurationService {

  protected final ClassLoader classLoader;
  protected final Path path;
  protected final ConfigurationLoaderFactory backingLoaderFactory;
  protected final URLConfigurationFactory factory;

  public DefaultConfigurationService(@NotNull ClassLoader classLoader, @NotNull Path path, @NotNull ConfigurationLoaderFactory backingLoaderFactory) {
    this.classLoader = Objects.requireNonNull(classLoader);
    this.path = Objects.requireNonNull(path);
    this.backingLoaderFactory = Objects.requireNonNull(backingLoaderFactory);
    this.factory = URLConfigurationFactory.newBuilder()
        .configurationLoaderFactory(this::createNewConfigurationLoader)
        .destContentRoot(this.path)
        .classLoader(this.classLoader)
        .build();
  }

  @SuppressWarnings("all")
  public AbstractConfigurationLoader.@NotNull Builder<?, ? extends AbstractConfigurationLoader<? extends @NotNull ConfigurationNode>> createNewConfigurationLoader() {
    AbstractConfigurationLoader.Builder loaderBuilder = this.backingLoaderFactory.create();

    ObjectMapper.Factory.Builder mapperFactoryBuilder = ObjectMapper.factoryBuilder();
    configureMapperFactoryBuilder(mapperFactoryBuilder);

    loaderBuilder.defaultOptions((UnaryOperator<ConfigurationOptions>) configurationOptions -> {
      return configurationOptions.serializers(serializers -> serializers.registerAnnotatedObjects(mapperFactoryBuilder.build()));
    });

    return loaderBuilder;
  }

  protected void configureMapperFactoryBuilder(@NotNull ObjectMapper.Factory.Builder builder) {}

  @Override
  protected ConfigurationNode createNode(String fallbackFile, String destinationFile) throws IOException {
    ConfigurationNode node = this.factory.loadConfigurationInFilesystem(destinationFile)
        .orElse(null);

    ConfigurationNode fallback = this.factory.loadConfigurationFromResource(fallbackFile)
        .orElse(null);

    if (node == null) {
      node = fallback;
      this.factory.saveConfigurationNodeInFilesystem(destinationFile, node);
    }

    return node;
  }

  protected void applyMissing(ConfigurationNode from, ConfigurationNode to) {
    for (var entry : from.childrenMap().entrySet()) {
      Object key = entry.getKey();
      ConfigurationNode fromChild = entry.getValue();
      ConfigurationNode toChild = to.node(key);

      if (toChild.virtual()) {
        toChild.raw(fromChild.raw());
      } else {
        applyMissing(fromChild, toChild);
      }
    }
  }
}