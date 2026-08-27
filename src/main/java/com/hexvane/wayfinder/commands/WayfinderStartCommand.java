package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.navigation.NavigationSession;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
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
    private final RequiredArg<String> routeArg;

    public WayfinderStartCommand(@Nonnull WayfinderPlugin plugin) {
        super("start", "Start navigating a route");
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
        NavigationSession session = plugin.getNavigationManager().startRoute(ref, routeName);
        if (session == null) {
            context.sendMessage(Message.raw("Route not found or has no waypoints: " + routeName));
            return;
        }

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
