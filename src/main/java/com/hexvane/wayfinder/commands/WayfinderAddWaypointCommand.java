package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

/**
 * Add a waypoint at the player's current position.
 * Usage: /wayfinder addwaypoint <route> [name]
 */
public class WayfinderAddWaypointCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;
    private final OptionalArg<String> nameArg;

    public WayfinderAddWaypointCommand(@Nonnull WayfinderPlugin plugin) {
        super("addwaypoint", "Add a waypoint at your position");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
        this.nameArg = this.withOptionalArg("name", "commands.wayfinder.arg.name.desc", ArgTypes.GREEDY_STRING);
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

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(Message.raw("Unable to determine your position."));
            return;
        }

        Vector3d pos = transform.getPosition();
        String waypointName = context.provided(nameArg) ? context.get(nameArg) : null;
        if (waypointName == null || waypointName.isBlank()) {
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
