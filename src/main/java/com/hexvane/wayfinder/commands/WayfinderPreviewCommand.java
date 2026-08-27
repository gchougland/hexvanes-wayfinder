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
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

/**
 * Preview a route by spawning particle markers at all waypoints.
 * Usage: /wayfinder preview <route>
 */
public class WayfinderPreviewCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;

    public WayfinderPreviewCommand(@Nonnull WayfinderPlugin plugin) {
        super("preview", "Preview all waypoints as particles");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
        requireNoPermission();
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

        if (route.getWaypointCount() == 0) {
            context.sendMessage(Message.raw("Route '" + routeName + "' has no waypoints."));
            return;
        }

        List<Ref<EntityStore>> targetPlayers = Collections.singletonList(ref);
        String particle = route.getParticle();

        for (int i = 0; i < route.getWaypointCount(); i++) {
            Waypoint wp = route.getWaypoint(i);
            Vector3d pos = new Vector3d(wp.getX(), wp.getY() + 1.0, wp.getZ());
            for (int j = 0; j < 3; j++) {
                ParticleUtil.spawnParticleEffect(particle, pos, targetPlayers, store);
            }
        }

        context.sendMessage(Message.raw("Previewing route '" + routeName +
                "' - " + route.getWaypointCount() + " waypoint markers spawned."));
    }
}
