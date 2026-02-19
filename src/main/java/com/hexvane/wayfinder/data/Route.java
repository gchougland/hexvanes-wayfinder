package com.hexvane.wayfinder.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A named, ordered sequence of waypoints.
 */
public class Route {
    private String name;
    private String displayName;
    private String visualMode;
    private String particle;
    private String completionCommand;
    private List<Waypoint> waypoints;

    public Route() {
        this.waypoints = new ArrayList<>();
        this.visualMode = "FLOATING";
        this.particle = "Wayfinder_Trail";
    }

    public Route(@Nonnull String name) {
        this.name = name;
        this.displayName = name;
        this.waypoints = new ArrayList<>();
        this.visualMode = "FLOATING";
        this.particle = "Wayfinder_Trail";
    }

    @Nonnull
    public String getName() {
        return name != null ? name : "";
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName != null ? displayName : getName();
    }

    public void setDisplayName(@Nonnull String displayName) {
        this.displayName = displayName;
    }

    @Nonnull
    public String getVisualMode() {
        return visualMode != null ? visualMode : "FLOATING";
    }

    public void setVisualMode(@Nonnull String visualMode) {
        this.visualMode = visualMode;
    }

    @Nonnull
    public String getParticle() {
        return particle != null ? particle : "Wayfinder_Trail";
    }

    public void setParticle(@Nonnull String particle) {
        this.particle = particle;
    }

    /**
     * Optional command to run when the player completes the entire route.
     * Executed as the player. Leading slash is optional.
     */
    @Nullable
    public String getCompletionCommand() {
        return completionCommand;
    }

    public void setCompletionCommand(@Nullable String completionCommand) {
        this.completionCommand = completionCommand;
    }

    @Nonnull
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void addWaypoint(@Nonnull Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public void removeWaypoint(int index) {
        if (index >= 0 && index < waypoints.size()) {
            waypoints.remove(index);
        }
    }

    @Nullable
    public Waypoint getWaypoint(int index) {
        if (index >= 0 && index < waypoints.size()) {
            return waypoints.get(index);
        }
        return null;
    }

    public int getWaypointCount() {
        return waypoints.size();
    }
}
