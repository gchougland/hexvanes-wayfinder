package com.hexvane.wayfinder;

import com.hexvane.wayfinder.commands.WayfinderCommand;
import com.hexvane.wayfinder.data.RouteStorage;
import com.hexvane.wayfinder.data.WayfinderConfig;
import com.hexvane.wayfinder.navigation.NavigationManager;
import com.hexvane.wayfinder.navigation.NavigationTickSystem;
import com.hexvane.wayfinder.navigation.WayfinderMarkerProvider;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WayfinderPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static WayfinderPlugin instance;

    private RouteStorage routeStorage;
    private WayfinderConfig config;
    private NavigationManager navigationManager;

    public WayfinderPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        // Initialize data
        config = WayfinderConfig.load(this.getDataDirectory());
        routeStorage = new RouteStorage(this.getDataDirectory());
        navigationManager = new NavigationManager(routeStorage);

        // Register commands
        this.getCommandRegistry().registerCommand(new WayfinderCommand(this));
        LOGGER.atInfo().log("Registered Wayfinder commands");

        // Register marker provider for new worlds
        this.getEventRegistry().registerGlobal(
                AddWorldEvent.class,
                (AddWorldEvent event) -> {
                    try {
                        event.getWorld().getWorldMapManager().addMarkerProvider(
                                "wayfinder", WayfinderMarkerProvider.INSTANCE);
                        LOGGER.atInfo().log("Registered Wayfinder marker provider for world: %s",
                                event.getWorld().getName());
                    } catch (Exception e) {
                        LOGGER.atWarning().log("Failed to register marker provider for world %s: %s",
                                event.getWorld().getName(), e.getMessage());
                    }
                }
        );
    }

    @Override
    protected void start() {
        // Register tick system here (not in setup) because Player component type
        // is not available during setup - it's registered by the Player module
        ComponentType<EntityStore, Player> playerType = Player.getComponentType();
        if (playerType != null) {
            this.getEntityStoreRegistry().registerSystem(new NavigationTickSystem(playerType));
            LOGGER.atInfo().log("Registered navigation tick system");
        } else {
            LOGGER.atSevere().log("Player component type not available - navigation tick system not registered");
        }

        // Register marker provider for existing worlds
        try {
            Universe universe = Universe.get();
            if (universe != null) {
                for (var worldEntry : universe.getWorlds().entrySet()) {
                    var world = worldEntry.getValue();
                    var worldMapManager = world.getWorldMapManager();
                    if (worldMapManager != null) {
                        worldMapManager.addMarkerProvider("wayfinder", WayfinderMarkerProvider.INSTANCE);
                        LOGGER.atInfo().log("Registered Wayfinder marker provider for world: %s",
                                world.getName());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.atWarning().log("Failed to register marker provider: %s", e.getMessage());
        }
    }

    @Override
    protected void shutdown() {
        config.save(this.getDataDirectory());
        LOGGER.atInfo().log("Wayfinder shutting down");
    }

    @Nullable
    public static WayfinderPlugin getInstance() {
        return instance;
    }

    @Nonnull
    public RouteStorage getRouteStorage() {
        return routeStorage;
    }

    @Nonnull
    public WayfinderConfig getConfig() {
        return config;
    }

    @Nonnull
    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}
