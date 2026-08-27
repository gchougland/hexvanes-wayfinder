package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
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
    private final RequiredArg<String> routeArg;

    public WayfinderCreateCommand(@Nonnull WayfinderPlugin plugin) {
        super("create", "Create a new empty route");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.routeName.desc", ArgTypes.STRING);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        String routeName = context.get(routeArg);
        if (plugin.getRouteStorage().get(routeName) != null) {
            context.sendMessage(Message.raw("Route '" + routeName + "' already exists."));
            return;
        }

        Route route = new Route(routeName);
        plugin.getRouteStorage().save(route);
        context.sendMessage(Message.raw("Created route: " + routeName));
    }
}
