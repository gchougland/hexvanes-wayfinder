package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Add a waypoint at the player's current position.
 * Usage: /wayfinder addwaypoint <route> [name]
 */
public class WayfinderAddWaypointCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderAddWaypointCommand(@Nonnull WayfinderPlugin plugin) {
        super("addwaypoint", "Add a waypoint at your position");
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
        if (rawArgs.startsWith("addwaypoint")) {
            rawArgs = rawArgs.substring(11).trim();
        }

        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder addwaypoint <route> [name]"));
            return;
        }

        // Parse: first word is route name, rest is optional waypoint name
        String[] parts = rawArgs.split("\\s+", 2);
        String routeName = parts[0];
        String waypointName = parts.length > 1 ? parts[1] : null;

        Route route = plugin.getRouteStorage().get(routeName);
        if (route == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(Message.raw("Unable to determine your position."));
            return;
        }

        Vector3d pos = transform.getPosition();
        if (waypointName == null) {
            waypointName = "Waypoint " + (route.getWaypointCount() + 1);
        }

        Waypoint waypoint = new Waypoint(pos.x, pos.y, pos.z, waypointName,
                plugin.getConfig().getDefaultArrivalRadius(), null);
        route.addWaypoint(waypoint);
        plugin.getRouteStorage().save(route);

        context.sendMessage(Message.raw("Added waypoint '" + waypointName + "' to route '" + routeName +
                "' at (" + String.format("%.1f, %.1f, %.1f", pos.x, pos.y, pos.z) +
                ") [" + route.getWaypointCount() + " total]"));
    }
}
