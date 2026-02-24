package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Waypoint;
import com.hexvane.wayfinder.navigation.NavigationSession;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Skip to the next waypoint in the current route.
 * Usage: /wayfinder skip
 */
public class WayfinderSkipCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderSkipCommand(@Nonnull WayfinderPlugin plugin) {
        super("skip", "Skip to the next waypoint");
        this.plugin = plugin;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        NavigationSession session = plugin.getNavigationManager().getSession(ref);
        if (session == null) {
            context.sendMessage(Message.raw("You are not currently navigating."));
            return;
        }

        if (session.skip()) {
            Waypoint next = session.getCurrentWaypoint();
            String wpName = next != null ? next.getName() : "unknown";
            context.sendMessage(Message.raw("Skipped to: " + wpName +
                    " (" + session.getProgressString() + ")"));
        } else {
            plugin.getNavigationManager().stopNavigation(ref);
            context.sendMessage(Message.raw("Route '" + session.getRoute().getName() +
                    "' completed (skipped last waypoint)."));
        }
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}
