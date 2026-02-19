package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Set the command to run when a player completes the entire route.
 * The command is executed as the player. Usage: /wayfinder setcompletioncommand <route> <command>
 */
public class WayfinderSetCompletionCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderSetCompletionCommand(@Nonnull WayfinderPlugin plugin) {
        super("setcompletioncommand", "Set command to run when route is completed");
        this.plugin = plugin;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        String rawArgs = CommandUtil.stripCommandName(context.getInputString()).trim();
        if (rawArgs.startsWith("setcompletioncommand")) {
            rawArgs = rawArgs.substring(20).trim();
        }

        String[] parts = rawArgs.split("\\s+", 2);
        if (parts.length < 1 || parts[0].isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder setcompletioncommand <route> [command]"));
            return;
        }

        String routeName = parts[0];
        Route route = plugin.getRouteStorage().get(routeName);
        if (route == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        String command = parts.length > 1 ? parts[1].trim() : "";
        route.setCompletionCommand(command.isEmpty() ? null : command);
        plugin.getRouteStorage().save(route);

        if (command.isEmpty()) {
            context.sendMessage(Message.raw("Cleared completion command for route '" + routeName + "'"));
        } else {
            context.sendMessage(Message.raw("Set completion command for route '" + routeName + "': " + command));
        }
    }
}
