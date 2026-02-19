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
 * Remove a waypoint by index.
 * Usage: /wayfinder removewaypoint <route> <index>
 */
public class WayfinderRemoveWPCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderRemoveWPCommand(@Nonnull WayfinderPlugin plugin) {
        super("removewaypoint", "Remove a waypoint by index");
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
        if (rawArgs.startsWith("removewaypoint")) {
            rawArgs = rawArgs.substring(14).trim();
        }

        String[] parts = rawArgs.split("\\s+");
        if (parts.length < 2) {
            context.sendMessage(Message.raw("Usage: /wayfinder removewaypoint <route> <index>"));
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
            index = Integer.parseInt(parts[1]) - 1; // 1-indexed for user
        } catch (NumberFormatException e) {
            context.sendMessage(Message.raw("Invalid index: " + parts[1]));
            return;
        }

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
