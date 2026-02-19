package com.hexvane.wayfinder.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A single point in a route with position, arrival radius, name, and optional message.
 */
public class Waypoint {
    private double x;
    private double y;
    private double z;
    private String name;
    private double radius;
    private String message;
    private String arrivalCommand;
    private String arrivalSound;

    public Waypoint() {
        this.radius = 3.0;
    }

    public Waypoint(double x, double y, double z, @Nonnull String name, double radius, @Nullable String message) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.radius = radius;
        this.message = message;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Nonnull
    public String getName() {
        return name != null ? name : "Waypoint";
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    /**
     * Optional command to run when the player reaches this waypoint.
     * Executed as the player. Leading slash is optional.
     */
    @Nullable
    public String getArrivalCommand() {
        return arrivalCommand;
    }

    public void setArrivalCommand(@Nullable String arrivalCommand) {
        this.arrivalCommand = arrivalCommand;
    }

    /**
     * Optional sound event ID to play when the player reaches this waypoint (e.g. from game assets).
     */
    @Nullable
    public String getArrivalSound() {
        return arrivalSound;
    }

    public void setArrivalSound(@Nullable String arrivalSound) {
        this.arrivalSound = arrivalSound;
    }

    public double distanceTo(double px, double py, double pz) {
        double dx = px - x;
        double dy = py - y;
        double dz = pz - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
