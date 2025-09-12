package io.github.ynverxe.commons.command;

import io.github.ynverxe.commons.service.AbstractService;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class CloudCommandService<T> extends AbstractService {

  private @MonotonicNonNull CommandManager<T> commandManager;
  private @MonotonicNonNull AnnotationParser<T> annotationParser;
  private final Class<T> commandSenderClass;

  public CloudCommandService(Class<T> commandSenderClass) {
    this.commandSenderClass = Objects.requireNonNull(commandSenderClass);
  }

  @Override
  protected void performStart() throws Exception {
    this.commandManager = createCommandManager();
    this.annotationParser = this.createAnnotationParser(this.commandManager, this.commandSenderClass);
  }

  protected abstract @NotNull CommandManager<T> createCommandManager();

  protected @NotNull AnnotationParser<T> createAnnotationParser(@NotNull CommandManager<T> commandManager, @NotNull Class<T> commandSenderClass) {
    return new AnnotationParser<>(commandManager, commandSenderClass);
  }

  public CommandManager<T> commandManager() {
    return commandManager;
  }

  public AnnotationParser<T> annotationParser() {
    return annotationParser;
  }

  protected void registerCommands(@NotNull Object @NotNull ... instances) {
    this.annotationParser.parse(instances).forEach(this::registerCommand);
  }

  protected void registerCommand(@NotNull Command<T> command) {
    this.commandManager.commandRegistrationHandler().registerCommand(command);
  }
}