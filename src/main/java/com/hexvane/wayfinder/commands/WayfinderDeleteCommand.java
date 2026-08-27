package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Delete a route.
 * Usage: /wayfinder delete <route>
 */
public class WayfinderDeleteCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;

    public WayfinderDeleteCommand(@Nonnull WayfinderPlugin plugin) {
        super("delete", "Delete a route");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        String routeName = context.get(routeArg);
        plugin.getRouteStorage().delete(routeName);
        context.sendMessage(Message.raw("Deleted route: " + routeName));
    }
}
