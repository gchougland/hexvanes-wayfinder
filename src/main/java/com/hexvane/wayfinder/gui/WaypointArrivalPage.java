package com.hexvane.wayfinder.gui;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.Waypoint;
import com.hexvane.wayfinder.navigation.NavigationSession;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * Arrival popup UI shown when a player reaches a waypoint with a message.
 */
public class WaypointArrivalPage extends InteractiveCustomUIPage<WaypointArrivalPage.ArrivalEventData> {
    private final World world;
    private final WayfinderPlugin plugin;
    private final Waypoint waypoint;
    private final NavigationSession session;

    public WaypointArrivalPage(
            @Nonnull PlayerRef playerRef,
            @Nonnull World world,
            @Nonnull WayfinderPlugin plugin,
            @Nonnull Waypoint waypoint,
            @Nonnull NavigationSession session) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, ArrivalEventData.CODEC);
        this.world = world;
        this.plugin = plugin;
        this.waypoint = waypoint;
        this.session = session;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder commandBuilder,
            @Nonnull UIEventBuilder eventBuilder,
            @Nonnull Store<EntityStore> store) {

        commandBuilder.append("Pages/WaypointArrivalPage.ui");

        // Set waypoint info
        commandBuilder.set("#WaypointName.Text", waypoint.getName());
        commandBuilder.set("#WaypointMessage.Text",
                waypoint.getMessage() != null ? waypoint.getMessage() : "");
        commandBuilder.set("#Progress.Text", session.getProgressString());

        // Continue button
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ContinueButton",
                EventData.of("Action", "Continue"),
                false
        );

        // Stop navigation button
        if (plugin.getConfig().isAllowPlayerDisable()) {
            commandBuilder.set("#StopButton.Visible", true);
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#StopButton",
                    EventData.of("Action", "StopNavigation"),
                    false
            );
        } else {
            commandBuilder.set("#StopButton.Visible", false);
        }
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull ArrivalEventData eventData) {

        String action = eventData.action;
        if (action == null) {
            return;
        }

        switch (action) {
            case "Continue" -> close();
            case "StopNavigation" -> {
                plugin.getNavigationManager().stopNavigation(ref);
                close();
            }
        }
    }

    public static class ArrivalEventData {
        public String action;

        public static final BuilderCodec<ArrivalEventData> CODEC = BuilderCodec.builder(
                ArrivalEventData.class, ArrivalEventData::new
        )
        .append(new KeyedCodec<>("Action", Codec.STRING),
                (data, s) -> data.action = s, data -> data.action)
        .add()
        .build();
    }
}
