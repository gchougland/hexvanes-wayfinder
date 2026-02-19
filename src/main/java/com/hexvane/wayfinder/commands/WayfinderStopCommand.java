package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
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
 * Stop current navigation.
 * Usage: /wayfinder stop
 */
public class WayfinderStopCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderStopCommand(@Nonnull WayfinderPlugin plugin) {
        super("stop", "Stop current navigation");
        this.plugin = plugin;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        if (!plugin.getNavigationManager().isNavigating(ref)) {
            context.sendMessage(Message.raw("You are not currently navigating."));
            return;
        }

        plugin.getNavigationManager().stopNavigation(ref);
        context.sendMessage(Message.raw("Navigation stopped."));
    }
}
