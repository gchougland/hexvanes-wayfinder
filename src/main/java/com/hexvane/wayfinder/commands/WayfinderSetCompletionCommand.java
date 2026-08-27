package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Set the command to run when a player completes the entire route.
 * Usage: /wayfinder setcompletioncommand <route> [command]
 */
public class WayfinderSetCompletionCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;
    private final OptionalArg<String> commandArg;

    public WayfinderSetCompletionCommand(@Nonnull WayfinderPlugin plugin) {
        super("setcompletioncommand", "Set command to run when route is completed");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
        this.commandArg = this.withOptionalArg("command", "commands.wayfinder.arg.command.desc", ArgTypes.GREEDY_STRING);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        String routeName = context.get(routeArg);
        Route route = plugin.getRouteStorage().get(routeName);
        if (route == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        String command = context.provided(commandArg) ? context.get(commandArg).trim() : "";
        route.setCompletionCommand(command.isEmpty() ? null : command);
        plugin.getRouteStorage().save(route);

        if (command.isEmpty()) {
            context.sendMessage(Message.raw("Cleared completion command for route '" + routeName + "'"));
        } else {
            context.sendMessage(Message.raw("Set completion command for route '" + routeName + "': " + command));
        }
    }
}
