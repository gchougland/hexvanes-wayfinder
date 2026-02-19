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
 * Set the sound event to play when a player reaches this waypoint.
 * Use the game's sound event ID (e.g. from assets). Usage: /wayfinder setsound <route> <index> [soundEventId]
 */
public class WayfinderSetSoundCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderSetSoundCommand(@Nonnull WayfinderPlugin plugin) {
        super("setsound", "Set arrival sound for a waypoint");
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
        if (rawArgs.startsWith("setsound")) {
            rawArgs = rawArgs.substring(8).trim();
        }

        String[] parts = rawArgs.split("\\s+", 3);
        if (parts.length < 2) {
            context.sendMessage(Message.raw("Usage: /wayfinder setsound <route> <index> [soundEventId]"));
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

        String soundId = parts.length > 2 ? parts[2].trim() : "";
        wp.setArrivalSound(soundId.isEmpty() ? null : soundId);
        plugin.getRouteStorage().save(route);

        if (soundId.isEmpty()) {
            context.sendMessage(Message.raw("Cleared arrival sound for waypoint '" + wp.getName() +
                    "' in route '" + routeName + "'"));
        } else {
            context.sendMessage(Message.raw("Set arrival sound for waypoint '" + wp.getName() +
                    "' in route '" + routeName + "': " + soundId));
        }
    }
}
