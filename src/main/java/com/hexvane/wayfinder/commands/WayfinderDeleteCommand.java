package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
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
 * Delete a route.
 * Usage: /wayfinder delete <route>
 */
public class WayfinderDeleteCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;

    public WayfinderDeleteCommand(@Nonnull WayfinderPlugin plugin) {
        super("delete", "Delete a route");
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
        if (rawArgs.startsWith("delete")) {
            rawArgs = rawArgs.substring(6).trim();
        }

        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.raw("Usage: /wayfinder delete <routeName>"));
            return;
        }

        String routeName = rawArgs;
        if (plugin.getRouteStorage().get(routeName) == null) {
            context.sendMessage(Message.raw("Route not found: " + routeName));
            return;
        }

        plugin.getRouteStorage().delete(routeName);
        context.sendMessage(Message.raw("Deleted route: " + routeName));
    }
}
