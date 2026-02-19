package com.hexvane.wayfinder.data;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

/**
 * Global configuration for the Wayfinder plugin.
 */
public class WayfinderConfig {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String CONFIG_FILE = "wayfinder_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Navigation settings
    private String defaultParticle = "Wayfinder_Trail";
    private double trailSpacing = 2.0;
    private double trailMaxDistance = 20.0;
    private double defaultArrivalRadius = 3.0;
    private String visualMode = "FLOATING";
    private int tickRate = 10;

    // UI settings
    private boolean showArrivalPopup = true;
    private int arrivalPopupTimeout = 0;
    private boolean allowPlayerDisable = true;

    // Persistence settings
    private boolean persistSessions = false;

    public WayfinderConfig() {
    }

    @Nonnull
    public String getDefaultParticle() {
        return defaultParticle;
    }

    public void setDefaultParticle(@Nonnull String defaultParticle) {
        this.defaultParticle = defaultParticle;
    }

    public double getTrailSpacing() {
        return trailSpacing;
    }

    public void setTrailSpacing(double trailSpacing) {
        this.trailSpacing = trailSpacing;
    }

    public double getTrailMaxDistance() {
        return trailMaxDistance;
    }

    public void setTrailMaxDistance(double trailMaxDistance) {
        this.trailMaxDistance = trailMaxDistance;
    }

    public double getDefaultArrivalRadius() {
        return defaultArrivalRadius;
    }

    public void setDefaultArrivalRadius(double defaultArrivalRadius) {
        this.defaultArrivalRadius = defaultArrivalRadius;
    }

    @Nonnull
    public String getVisualMode() {
        return visualMode;
    }

    public void setVisualMode(@Nonnull String visualMode) {
        this.visualMode = visualMode;
    }

    public int getTickRate() {
        return tickRate;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public boolean isShowArrivalPopup() {
        return showArrivalPopup;
    }

    public void setShowArrivalPopup(boolean showArrivalPopup) {
        this.showArrivalPopup = showArrivalPopup;
    }

    public int getArrivalPopupTimeout() {
        return arrivalPopupTimeout;
    }

    public void setArrivalPopupTimeout(int arrivalPopupTimeout) {
        this.arrivalPopupTimeout = arrivalPopupTimeout;
    }

    public boolean isAllowPlayerDisable() {
        return allowPlayerDisable;
    }

    public void setAllowPlayerDisable(boolean allowPlayerDisable) {
        this.allowPlayerDisable = allowPlayerDisable;
    }

    public boolean isPersistSessions() {
        return persistSessions;
    }

    public void setPersistSessions(boolean persistSessions) {
        this.persistSessions = persistSessions;
    }

    @Nonnull
    public static WayfinderConfig load(@Nonnull Path dataDirectory) {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            LOGGER.atSevere().log("Failed to create data directory: %s", e.getMessage());
            return new WayfinderConfig();
        }

        Path configFile = dataDirectory.resolve(CONFIG_FILE);
        if (!Files.exists(configFile)) {
            WayfinderConfig config = new WayfinderConfig();
            config.save(dataDirectory);
            return config;
        }

        try {
            String json = Files.readString(configFile);
            WayfinderConfig config = GSON.fromJson(json, WayfinderConfig.class);
            return config != null ? config : new WayfinderConfig();
        } catch (Exception e) {
            LOGGER.atWarning().log("Failed to load config: %s", e.getMessage());
            return new WayfinderConfig();
        }
    }

    public void save(@Nonnull Path dataDirectory) {
        try {
            Files.createDirectories(dataDirectory);
            Path configFile = dataDirectory.resolve(CONFIG_FILE);
            String json = GSON.toJson(this);
            Files.writeString(configFile, json);
        } catch (Exception e) {
            LOGGER.atWarning().log("Failed to save config: %s", e.getMessage());
        }
    }
}
