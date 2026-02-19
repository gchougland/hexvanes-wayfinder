package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.navigation.NavigationSession;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Start navigating a route.
 * Usage: /wayfinder start <route>
 */
public class WayfinderStartCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderStartCommand(@Nonnull WayfinderPlugin plugin) {
        super("start", "Start navigating a route");
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
        if (rawArgs.startsWith("start")) {
            rawArgs = rawArgs.substring(5).trim();
        }

        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder start <route>"));
            return;
        }

        String routeName = rawArgs;
        NavigationSession session = plugin.getNavigationManager().startRoute(ref, routeName);
        if (session == null) {
            context.sendMessage(Message.raw("Route not found or has no waypoints: " + routeName));
            return;
        }

        // Register player for cross-thread marker lookups
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            plugin.getNavigationManager().registerPlayer(ref, player);
        }

        var waypoint = session.getCurrentWaypoint();
        String wpName = waypoint != null ? waypoint.getName() : "unknown";
        context.sendMessage(Message.raw("Started navigation on route '" + routeName +
                "'. Head to: " + wpName + " (" + session.getProgressString() + ")"));
    }
}
