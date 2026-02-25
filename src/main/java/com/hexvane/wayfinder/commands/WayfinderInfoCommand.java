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
 * Show details of a route.
 * Usage: /wayfinder info <route>
 */
public class WayfinderInfoCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderInfoCommand(@Nonnull WayfinderPlugin plugin) {
        super("info", "Show route details");
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
        if (rawArgs.startsWith("info")) {
            rawArgs = rawArgs.substring(4).trim();
        }

        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder info <route>"));
            return;
        }

        String routeName = rawArgs;
        Route route = plugin.getRouteStorage().get(routeName);
        if (route == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Route: ").append(route.getName()).append("\n");
        sb.append("Visual Mode: ").append(route.getVisualMode()).append("\n");
        sb.append("Particle: ").append(route.getParticle()).append("\n");
        sb.append("Waypoints (").append(route.getWaypointCount()).append("):\n");

        for (int i = 0; i < route.getWaypointCount(); i++) {
            Waypoint wp = route.getWaypoint(i);
            sb.append("  ").append(i + 1).append(". ").append(wp.getName());
            sb.append(" (").append(String.format("%.1f, %.1f, %.1f", wp.getX(), wp.getY(), wp.getZ()));
            sb.append(") r=").append(String.format("%.1f", wp.getRadius()));
            if (wp.getMessage() != null) {
                sb.append(" [has message]");
            }
            sb.append("\n");
        }

        context.sendMessage(Message.raw(sb.toString().trim()));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}
