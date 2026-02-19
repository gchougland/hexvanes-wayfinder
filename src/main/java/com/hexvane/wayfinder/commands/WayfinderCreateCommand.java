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
 * Create a new empty route.
 * Usage: /wayfinder create <route>
 */
public class WayfinderCreateCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderCreateCommand(@Nonnull WayfinderPlugin plugin) {
        super("create", "Create a new empty route");
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
        if (rawArgs.startsWith("create")) {
            rawArgs = rawArgs.substring(6).trim();
        }

        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder create <routeName>"));
            return;
        }

        String routeName = rawArgs;
        if (plugin.getRouteStorage().get(routeName) != null) {
            context.sendMessage(Message.raw("Route '" + routeName + "' already exists."));
            return;
        }

        Route route = new Route(routeName);
        plugin.getRouteStorage().save(route);
        context.sendMessage(Message.raw("Created route: " + routeName));
    }
}
