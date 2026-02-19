package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
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
 * Set arrival radius for a waypoint.
 * Usage: /wayfinder setradius <route> <index> <radius>
 */
public class WayfinderSetRadiusCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderSetRadiusCommand(@Nonnull WayfinderPlugin plugin) {
        super("setradius", "Set arrival radius for a waypoint");
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
        if (rawArgs.startsWith("setradius")) {
            rawArgs = rawArgs.substring(9).trim();
        }

        String[] parts = rawArgs.split("\\s+");
        if (parts.length < 3) {
            context.sendMessage(Message.raw("Usage: /wayfinder setradius <route> <index> <radius>"));
            return;
        }

        String routeName = parts[0];
        Route route = plugin.getRouteStorage().get(routeName);
        if (route == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        int index;
        try {
            index = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            context.sendMessage(Message.raw("Invalid index: " + parts[1]));
            return;
        }

        Waypoint wp = route.getWaypoint(index);
        if (wp == null) {
            context.sendMessage(Message.raw("Index out of range. Route has " +
                    route.getWaypointCount() + " waypoints."));
            return;
        }

        double radius;
        try {
            radius = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            context.sendMessage(Message.raw("Invalid radius: " + parts[2]));
            return;
        }

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
