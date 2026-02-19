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
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Preview a route by spawning particle markers at all waypoints.
 * Usage: /wayfinder preview <route>
 */
public class WayfinderPreviewCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderPreviewCommand(@Nonnull WayfinderPlugin plugin) {
        super("preview", "Preview all waypoints as particles");
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
        if (rawArgs.startsWith("preview")) {
            rawArgs = rawArgs.substring(7).trim();
        }

        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder preview <route>"));
            return;
        }

        String routeName = rawArgs;
        Route route = plugin.getRouteStorage().get(routeName);
        if (route == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        if (route.getWaypointCount() == 0) {
            context.sendMessage(Message.raw("Route '" + routeName + "' has no waypoints."));
            return;
        }

        // Spawn particles at each waypoint (only visible to this player)
        List<Ref<EntityStore>> targetPlayers = Collections.singletonList(ref);
        String particle = route.getParticle();

        for (int i = 0; i < route.getWaypointCount(); i++) {
            Waypoint wp = route.getWaypoint(i);
            Vector3d pos = new Vector3d(wp.getX(), wp.getY() + 1.0, wp.getZ());
            // Spawn multiple particles for visibility
            for (int j = 0; j < 3; j++) {
                ParticleUtil.spawnParticleEffect(particle, pos, targetPlayers, store);
            }
        }

        context.sendMessage(Message.raw("Previewing route '" + routeName +
                "' - " + route.getWaypointCount() + " waypoint markers spawned."));
    }
}
