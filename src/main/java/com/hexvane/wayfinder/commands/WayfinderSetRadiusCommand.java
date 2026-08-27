package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
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
 * Set arrival radius for a waypoint.
 * Usage: /wayfinder setradius <route> <index> <radius>
 */
public class WayfinderSetRadiusCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;
    private final RequiredArg<Integer> indexArg;
    private final RequiredArg<Double> radiusArg;

    public WayfinderSetRadiusCommand(@Nonnull WayfinderPlugin plugin) {
        super("setradius", "Set arrival radius for a waypoint");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
        this.indexArg = this.withRequiredArg("index", "commands.wayfinder.arg.index.desc", WayfinderArgTypes.WAYPOINT_INDEX);
        this.radiusArg = this.withRequiredArg("radius", "commands.wayfinder.arg.radius.desc", ArgTypes.DOUBLE);
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

        Waypoint wp = route.getWaypoint(context.get(indexArg) - 1);
        if (wp == null) {
            context.sendMessage(Message.raw("Index out of range. Route has " +
                    route.getWaypointCount() + " waypoints."));
            return;
        }

        double radius = context.get(radiusArg);
        if (radius <= 0) {
            context.sendMessage(Message.raw("Radius must be positive."));
            return;
        }

        wp.setRadius(radius);
        plugin.getRouteStorage().save(route);

        context.sendMessage(Message.raw("Set radius for waypoint '" + wp.getName() +
                "' to " + String.format("%.1f", radius) + " blocks"));
    }
}
