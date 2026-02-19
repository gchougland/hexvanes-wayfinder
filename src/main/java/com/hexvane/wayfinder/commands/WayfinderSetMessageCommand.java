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
 * Set the arrival message for a waypoint.
 * Usage: /wayfinder setmessage <route> <index> <message>
 */
public class WayfinderSetMessageCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderSetMessageCommand(@Nonnull WayfinderPlugin plugin) {
        super("setmessage", "Set arrival message for a waypoint");
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
        if (rawArgs.startsWith("setmessage")) {
            rawArgs = rawArgs.substring(10).trim();
        }

        String[] parts = rawArgs.split("\\s+", 3);
        if (parts.length < 3) {
            context.sendMessage(Message.raw("Usage: /wayfinder setmessage <route> <index> <message>"));
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

        String message = parts[2];
        wp.setMessage(message);
        plugin.getRouteStorage().save(route);

        context.sendMessage(Message.raw("Set message for waypoint '" + wp.getName() +
                "' in route '" + routeName + "'"));
    }
}
