package io.github.ynverxe.commons.command;

import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("all")
public abstract class PaperCloudCommandService extends CloudCommandService<Source> {

  private final @NotNull Plugin plugin;

  public PaperCloudCommandService(@NotNull Plugin plugin) {
    super(Source.class);
    this.plugin = Objects.requireNonNull(plugin);
  }

  @Override
  protected @NotNull CommandManager<Source> createCommandManager() {
    PaperCommandManager<Source> commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
        .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
        .buildOnEnable(this.plugin);

    return commandManager;
  }

  @Override
  public PaperCommandManager<Source> commandManager() {
    return (PaperCommandManager<Source>) super.commandManager();
  }
}