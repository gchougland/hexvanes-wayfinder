package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * List all defined routes.
 * Usage: /wayfinder list
 */
public class WayfinderListCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderListCommand(@Nonnull WayfinderPlugin plugin) {
        super("list", "List all routes");
        this.plugin = plugin;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        List<String> routes = plugin.getRouteStorage().listAll();
        if (routes.isEmpty()) {
            context.sendMessage(Message.raw("No routes defined. Use /wayfinder create <name> to create one."));
            return;
        }

        StringBuilder sb = new StringBuilder("Routes (" + routes.size() + "):\n");
        for (String name : routes) {
            Route route = plugin.getRouteStorage().get(name);
            int wpCount = route != null ? route.getWaypointCount() : 0;
            sb.append("  - ").append(name).append(" (").append(wpCount).append(" waypoints)\n");
        }
        context.sendMessage(Message.raw(sb.toString().trim()));
    }
}
