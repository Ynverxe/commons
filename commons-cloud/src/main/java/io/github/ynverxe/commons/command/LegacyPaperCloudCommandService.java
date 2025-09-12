package io.github.ynverxe.commons.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class LegacyPaperCloudCommandService<T> extends CloudCommandService<T> {

  private final Plugin plugin;

  public LegacyPaperCloudCommandService(@NotNull Class<T> commandSenderClass, @NotNull Plugin plugin) {
    super(commandSenderClass);
    this.plugin = Objects.requireNonNull(plugin);
  }

  @Override
  protected @NotNull CommandManager<T> createCommandManager() {
    LegacyPaperCommandManager<T> commandManager = new LegacyPaperCommandManager<>(this.plugin, (ExecutionCoordinator<T>) ExecutionCoordinator.nonSchedulingExecutor(), senderMapper());

    if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
      commandManager.registerBrigadier();
    } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
      commandManager.registerAsynchronousCompletions();
    }

    return commandManager;
  }

  @Override
  public LegacyPaperCommandManager<T> commandManager() {
    return (LegacyPaperCommandManager<T>) super.commandManager();
  }

  protected abstract SenderMapper<CommandSender, T> senderMapper();

  public abstract static class Native extends LegacyPaperCloudCommandService<CommandSender> {
    public Native(@NotNull Class<CommandSender> commandSenderClass, @NotNull Plugin plugin) {
      super(commandSenderClass, plugin);
    }

    @Override
    protected SenderMapper<CommandSender, CommandSender> senderMapper() {
      return SenderMapper.identity();
    }
  }
}