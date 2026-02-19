package com.hexvane.wayfinder.data;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Persists routes as individual JSON files in {pluginDataDir}/routes/.
 */
public class RouteStorage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String ROUTES_DIR = "routes";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dataDirectory;
    private final Map<String, Route> routes = new ConcurrentHashMap<>();

    public RouteStorage(@Nonnull Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        loadAll();
    }

    public void save(@Nonnull Route route) {
        routes.put(route.getName(), route);
        saveToFile(route);
    }

    @Nullable
    public Route get(@Nonnull String name) {
        return routes.get(name);
    }

    public void delete(@Nonnull String name) {
        routes.remove(name);
        deleteFile(name);
    }

    @Nonnull
    public List<String> listAll() {
        return new ArrayList<>(routes.keySet());
    }

    @Nonnull
    public Map<String, Route> getAll() {
        return routes;
    }

    private void saveToFile(@Nonnull Route route) {
        try {
            Path routesDir = dataDirectory.resolve(ROUTES_DIR);
            Files.createDirectories(routesDir);

            String fileName = sanitize(route.getName()) + ".json";
            Path routeFile = routesDir.resolve(fileName);
            String json = GSON.toJson(route);
            Files.writeString(routeFile, json);
            LOGGER.atInfo().log("Saved route '%s' to %s", route.getName(), routeFile);
        } catch (Exception e) {
            LOGGER.atWarning().log("Failed to save route '%s': %s", route.getName(), e.getMessage());
        }
    }

    private void deleteFile(@Nonnull String name) {
        try {
            Path routesDir = dataDirectory.resolve(ROUTES_DIR);
            String fileName = sanitize(name) + ".json";
            Path routeFile = routesDir.resolve(fileName);
            Files.deleteIfExists(routeFile);
        } catch (Exception e) {
            LOGGER.atWarning().log("Failed to delete route file '%s': %s", name, e.getMessage());
        }
    }

    private void loadAll() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            LOGGER.atSevere().log("Failed to create data directory: %s", e.getMessage());
            return;
        }

        Path routesDir = dataDirectory.resolve(ROUTES_DIR);
        if (!Files.exists(routesDir)) {
            LOGGER.atInfo().log("No routes directory found, starting fresh");
            return;
        }

        try (Stream<Path> files = Files.list(routesDir)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(file -> {
                try {
                    String json = Files.readString(file);
                    Route route = GSON.fromJson(json, Route.class);
                    if (route != null && route.getName() != null && !route.getName().isEmpty()) {
                        routes.put(route.getName(), route);
                    }
                } catch (Exception e) {
                    LOGGER.atWarning().log("Failed to load route from %s: %s", file.getFileName(), e.getMessage());
                }
            });
            LOGGER.atInfo().log("Loaded %d routes", routes.size());
        } catch (IOException e) {
            LOGGER.atWarning().log("Failed to list route files: %s", e.getMessage());
        }
    }

    private String sanitize(@Nonnull String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
