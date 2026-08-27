package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Route;
import com.hexvane.wayfinder.data.RouteStorage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionResult;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Argument types with tab completion for Wayfinder commands.
 */
public final class WayfinderArgTypes {
    private WayfinderArgTypes() {
    }

    public static final SingleArgumentType<String> ROUTE_NAME = new SingleArgumentType<>(
            "commands.wayfinder.argtype.route.name",
            "commands.wayfinder.argtype.route.usage",
            "Village_Tour"
    ) {
        @Nullable
        @Override
        public String parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            RouteStorage storage = storage();
            if (storage == null || storage.get(input) == null) {
                parseResult.fail(Message.raw("Route not found: " + input));
                return null;
            }
            return input;
        }

        @Override
        public void suggest(
                @Nonnull CommandSender sender,
                @Nonnull String textAlreadyEntered,
                int numParametersTyped,
                @Nonnull SuggestionResult result) {
            SuggestionUtil.suggestFiltered(routeNames(), textAlreadyEntered, result);
        }

        @Override
        public int getSuggestionValueCount() {
            return routeNames().size();
        }
    };

    public static final SingleArgumentType<Integer> WAYPOINT_INDEX = new SingleArgumentType<>(
            "commands.wayfinder.argtype.index.name",
            "commands.wayfinder.argtype.index.usage",
            "1",
            "2"
    ) {
        @Nullable
        @Override
        public Integer parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            try {
                int index = Integer.parseInt(input.trim());
                if (index < 1) {
                    parseResult.fail(Message.raw("Waypoint index must be 1 or greater."));
                    return null;
                }
                return index;
            } catch (NumberFormatException e) {
                parseResult.fail(Message.raw("Invalid waypoint index: " + input));
                return null;
            }
        }

        @Override
        public void suggest(
                @Nonnull CommandSender sender,
                @Nonnull String textAlreadyEntered,
                int numParametersTyped,
                @Nonnull SuggestionResult result) {
            SuggestionUtil.suggestFiltered(waypointIndexSuggestions(), textAlreadyEntered, result);
        }

        @Override
        public int getSuggestionValueCount() {
            return waypointIndexSuggestions().size();
        }
    };

    @Nullable
    private static RouteStorage storage() {
        WayfinderPlugin plugin = WayfinderPlugin.getInstance();
        return plugin != null ? plugin.getRouteStorage() : null;
    }

    @Nonnull
    private static List<String> routeNames() {
        RouteStorage storage = storage();
        if (storage == null) {
            return Collections.emptyList();
        }
        List<String> names = storage.listAll();
        Collections.sort(names);
        return names;
    }

    @Nonnull
    private static List<String> waypointIndexSuggestions() {
        RouteStorage storage = storage();
        if (storage == null) {
            return Collections.emptyList();
        }
        int max = 0;
        for (Route route : storage.getAll().values()) {
            max = Math.max(max, route.getWaypointCount());
        }
        if (max == 0) {
            return Collections.emptyList();
        }
        List<String> indices = new ArrayList<>(max);
        for (int i = 1; i <= max; i++) {
            indices.add(String.valueOf(i));
        }
        return indices;
    }
}
