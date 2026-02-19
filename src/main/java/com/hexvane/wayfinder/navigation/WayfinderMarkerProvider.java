package com.hexvane.wayfinder.navigation;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Waypoint;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerBuilder;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MarkersCollector;
import javax.annotation.Nonnull;

/**
 * Provides compass markers for each player's active navigation target.
 * Runs on the WorldMap thread — must NOT access the ECS store.
 */
public class WayfinderMarkerProvider implements WorldMapManager.MarkerProvider {
    public static final WayfinderMarkerProvider INSTANCE = new WayfinderMarkerProvider();
    private static final String MARKER_PREFIX = "wayfinder_";

    private WayfinderMarkerProvider() {
    }

    @Override
    public void update(
            @Nonnull World world,
            @Nonnull Player player,
            @Nonnull MarkersCollector collector) {

        WayfinderPlugin plugin = WayfinderPlugin.getInstance();
        if (plugin == null) {
            return;
        }

        NavigationSession session = plugin.getNavigationManager().getSessionByPlayer(player);
        if (session == null) {
            return;
        }

        Waypoint target = session.getCurrentWaypoint();
        if (target == null) {
            return;
        }

        // Include waypoint index in the marker ID so advancing to the next waypoint
        // causes the old marker to be removed and the new one to be added fresh.
        String markerId = MARKER_PREFIX + session.getRoute().getName() + "_" + session.getCurrentWaypointIndex();
        Transform transform = new Transform(target.getX(), target.getY(), target.getZ());
        MapMarker marker = new MapMarkerBuilder(markerId, "Coordinate.png", transform)
                .withCustomName(target.getName())
                .build();

        collector.add(marker);
    }
}
