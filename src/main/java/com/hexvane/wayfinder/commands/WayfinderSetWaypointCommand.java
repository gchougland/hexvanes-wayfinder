package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

/**
 * Move a waypoint to the player's current position.
 * Usage: /wayfinder setwaypoint <route> <index>
 */
public class WayfinderSetWaypointCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;
    private final RequiredArg<Integer> indexArg;

    public WayfinderSetWaypointCommand(@Nonnull WayfinderPlugin plugin) {
        super("setwaypoint", "Move a waypoint to your position");
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

        Waypoint wp = route.getWaypoint(context.get(indexArg) - 1);
        if (wp == null) {
            context.sendMessage(Message.raw("Index out of range. Route has " +
                    route.getWaypointCount() + " waypoints."));
            return;
        }

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(Message.raw("Unable to determine your position."));
            return;
        }

        Vector3d pos = transform.getPosition();
        wp.setX(pos.x);
        wp.setY(pos.y);
        wp.setZ(pos.z);
        plugin.getRouteStorage().save(route);

        context.sendMessage(Message.raw("Moved waypoint '" + wp.getName() + "' to (" +
                String.format("%.1f, %.1f, %.1f", pos.x, pos.y, pos.z) + ")"));
    }
}
