package com.hexvane.wayfinder.navigation;

import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.Waypoint;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Per-player runtime state tracking which route they're on and which waypoint is active.
 */
public class NavigationSession {
    private final Route route;
    private volatile int currentWaypointIndex;

    public NavigationSession(@Nonnull Route route) {
        this.route = route;
        this.currentWaypointIndex = 0;
    }

    @Nonnull
    public Route getRoute() {
        return route;
    }

    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    @Nullable
    public Waypoint getCurrentWaypoint() {
        return route.getWaypoint(currentWaypointIndex);
    }

    /**
     * Advances to the next waypoint in the route.
     * @return true if there was a next waypoint, false if the route is complete
     */
    public boolean advance() {
        if (currentWaypointIndex + 1 < route.getWaypointCount()) {
            currentWaypointIndex++;
            return true;
        }
        return false;
    }

    /**
     * Skips to the next waypoint without any event processing.
     * @return true if there was a next waypoint to skip to
     */
    public boolean skip() {
        return advance();
    }

    public boolean isComplete() {
        return currentWaypointIndex >= route.getWaypointCount();
    }

    public int getTotalWaypoints() {
        return route.getWaypointCount();
    }

    @Nonnull
    public String getProgressString() {
        return "Waypoint " + (currentWaypointIndex + 1) + " / " + route.getWaypointCount();
    }
}
