package com.hexvane.wayfinder.navigation;

import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.RouteStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Manages per-player navigation sessions.
 */
public class NavigationManager {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final RouteStorage routeStorage;
    private final Map<Ref<EntityStore>, NavigationSession> sessions = new ConcurrentHashMap<>();
    private final Map<Player, NavigationSession> playerSessions = new ConcurrentHashMap<>();

    public NavigationManager(@Nonnull RouteStorage routeStorage) {
        this.routeStorage = routeStorage;
    }

    /**
     * Start a player navigating a route by name.
     * @return the created session, or null if the route doesn't exist or has no waypoints
     */
    @Nullable
    public NavigationSession startRoute(@Nonnull Ref<EntityStore> playerRef, @Nonnull String routeName) {
        Route route = routeStorage.get(routeName);
        if (route == null) {
            LOGGER.atWarning().log("Route not found: %s", routeName);
            return null;
        }
        if (route.getWaypointCount() == 0) {
            LOGGER.atWarning().log("Route '%s' has no waypoints", routeName);
            return null;
        }

        NavigationSession session = new NavigationSession(route);
        sessions.put(playerRef, session);
        LOGGER.atInfo().log("Started navigation on route '%s'", routeName);
        return session;
    }

    /**
     * Associate a Player instance with the current session for cross-thread lookups.
     * Call this from the world thread after starting a route.
     */
    public void registerPlayer(@Nonnull Ref<EntityStore> playerRef, @Nonnull Player player) {
        NavigationSession session = sessions.get(playerRef);
        if (session != null) {
            playerSessions.put(player, session);
        }
    }

    /**
     * Stop navigation for a player.
     */
    public void stopNavigation(@Nonnull Ref<EntityStore> playerRef) {
        NavigationSession removed = sessions.remove(playerRef);
        if (removed != null) {
            LOGGER.atInfo().log("Stopped navigation on route '%s'", removed.getRoute().getName());
            playerSessions.values().remove(removed);
        }
    }

    /**
     * Get the active navigation session for a player.
     */
    @Nullable
    public NavigationSession getSession(@Nonnull Ref<EntityStore> playerRef) {
        return sessions.get(playerRef);
    }

    /**
     * Check if a player has an active navigation session.
     */
    public boolean isNavigating(@Nonnull Ref<EntityStore> playerRef) {
        return sessions.containsKey(playerRef);
    }

    /**
     * Get all active sessions (for tick system iteration).
     */
    @Nonnull
    public Map<Ref<EntityStore>, NavigationSession> getSessions() {
        return sessions;
    }

    /**
     * Get session by Player instance (thread-safe, for use from WorldMap thread).
     */
    @Nullable
    public NavigationSession getSessionByPlayer(@Nonnull Player player) {
        return playerSessions.get(player);
    }

    /**
     * Remove session for a player (e.g., on disconnect).
     */
    public void removeSession(@Nonnull Ref<EntityStore> playerRef) {
        NavigationSession removed = sessions.remove(playerRef);
        if (removed != null) {
            playerSessions.values().remove(removed);
        }
    }
}
