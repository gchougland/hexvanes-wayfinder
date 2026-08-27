package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
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
 * Remove a waypoint by index.
 * Usage: /wayfinder removewaypoint <route> <index>
 */
public class WayfinderRemoveWPCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;
    private final RequiredArg<Integer> indexArg;

    public WayfinderRemoveWPCommand(@Nonnull WayfinderPlugin plugin) {
        super("removewaypoint", "Remove a waypoint by index");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
        this.indexArg = this.withRequiredArg("index", "commands.wayfinder.arg.index.desc", WayfinderArgTypes.WAYPOINT_INDEX);
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

        int index = context.get(indexArg) - 1;
        if (index < 0 || index >= route.getWaypointCount()) {
            context.sendMessage(Message.raw("Index out of range. Route has " +
                    route.getWaypointCount() + " waypoints."));
            return;
        }

        String wpName = route.getWaypoint(index).getName();
        route.removeWaypoint(index);
        plugin.getRouteStorage().save(route);
        context.sendMessage(Message.raw("Removed waypoint '" + wpName + "' from route '" + routeName + "'"));
    }
}
