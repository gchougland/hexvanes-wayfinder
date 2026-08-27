package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Set the sound event to play when a player reaches this waypoint.
 * Usage: /wayfinder setsound <route> <index> [soundEventId]
 */
public class WayfinderSetSoundCommand extends AbstractPlayerCommand {
    private final WayfinderPlugin plugin;
    private final RequiredArg<String> routeArg;
    private final RequiredArg<Integer> indexArg;
    private final OptionalArg<SoundEvent> soundArg;

    public WayfinderSetSoundCommand(@Nonnull WayfinderPlugin plugin) {
        super("setsound", "Set arrival sound for a waypoint");
        this.plugin = plugin;
        this.routeArg = this.withRequiredArg("route", "commands.wayfinder.arg.route.desc", WayfinderArgTypes.ROUTE_NAME);
        this.indexArg = this.withRequiredArg("index", "commands.wayfinder.arg.index.desc", WayfinderArgTypes.WAYPOINT_INDEX);
        this.soundArg = this.withOptionalArg("sound", "commands.wayfinder.arg.sound.desc", ArgTypes.SOUND_EVENT_ASSET);
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

        String soundId = "";
        if (context.provided(soundArg)) {
            SoundEvent soundEvent = context.get(soundArg);
            if (soundEvent != null) {
                soundId = soundEvent.getId();
            }
        }
        wp.setArrivalSound(soundId == null || soundId.isEmpty() ? null : soundId);
        plugin.getRouteStorage().save(route);

        if (soundId == null || soundId.isEmpty()) {
            context.sendMessage(Message.raw("Cleared arrival sound for waypoint '" + wp.getName() +
                    "' in route '" + routeName + "'"));
        } else {
            context.sendMessage(Message.raw("Set arrival sound for waypoint '" + wp.getName() +
                    "' in route '" + routeName + "': " + soundId));
        }
    }
}
