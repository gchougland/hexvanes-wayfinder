package com.hexvane.wayfinder.navigation;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hexvane.wayfinder.data.WayfinderConfig;
import com.hexvane.wayfinder.data.Waypoint;
import com.hexvane.wayfinder.gui.WaypointArrivalPage;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.protocol.SoundCategory;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Tick system that handles particle trail rendering and waypoint arrival detection.
 * Iterates over all player entities each tick, checks if they have active navigation,
 * and spawns particles / checks arrival.
 */
public class NavigationTickSystem extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String ARRIVAL_PARTICLE = "Wayfinder_Arrival";
    /** Default sound when no waypoint arrival sound is set. Use a game sound event ID. */
    private static final String DEFAULT_ARRIVAL_SOUND = "SFX_Creative_Play_Add_Mask";

    @Nonnull
    private final ComponentType<EntityStore, Player> playerComponentType;
    private int tickCounter = 0;

    public NavigationTickSystem(@Nonnull ComponentType<EntityStore, Player> playerComponentType) {
        this.playerComponentType = playerComponentType;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return playerComponentType;
    }

    @Override
    public void tick(
            float dt,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        WayfinderPlugin plugin = WayfinderPlugin.getInstance();
        if (plugin == null) {
            return;
        }

        NavigationManager navManager = plugin.getNavigationManager();
        WayfinderConfig config = plugin.getConfig();

        // Only process on configured tick rate
        tickCounter++;
        if (tickCounter < config.getTickRate()) {
            return;
        }
        tickCounter = 0;

        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        if (!playerRef.isValid()) {
            return;
        }

        NavigationSession session = navManager.getSession(playerRef);
        if (session == null) {
            return;
        }

        Waypoint target = session.getCurrentWaypoint();
        if (target == null) {
            navManager.stopNavigation(playerRef);
            return;
        }

        TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
        if (transform == null) {
            return;
        }

        Vector3d playerPos = transform.getPosition();
        double distance = target.distanceTo(playerPos.x, playerPos.y, playerPos.z);

        // Arrival check
        if (distance <= target.getRadius()) {
            handleArrival(playerRef, store, session, target, plugin);
            return;
        }

        // Spawn particle trail
        spawnTrail(playerRef, playerPos, target, store, config);
    }

    private void handleArrival(
            @Nonnull Ref<EntityStore> playerRef,
            @Nonnull Store<EntityStore> store,
            @Nonnull NavigationSession session,
            @Nonnull Waypoint waypoint,
            @Nonnull WayfinderPlugin plugin) {

        WayfinderConfig config = plugin.getConfig();

        // Spawn arrival burst effect at waypoint position
        List<Ref<EntityStore>> targetPlayers = Collections.singletonList(playerRef);
        Vector3d arrivalPos = new Vector3d(waypoint.getX(), waypoint.getY() + 1.0, waypoint.getZ());
        ParticleUtil.spawnParticleEffect(ARRIVAL_PARTICLE, arrivalPos, targetPlayers, store);

        // Play arrival sound: waypoint-specific or default (2D so the player always hears it)
        String arrivalSound = waypoint.getArrivalSound();
        if (arrivalSound == null || arrivalSound.isEmpty()) {
            arrivalSound = DEFAULT_ARRIVAL_SOUND;
        }
        if (!arrivalSound.isEmpty()) {
            int soundIndex = SoundEvent.getAssetMap().getIndex(arrivalSound);
            if (soundIndex > 0) {
                PlayerRef pRef = store.getComponent(playerRef, PlayerRef.getComponentType());
                if (pRef != null) {
                    SoundUtil.playSoundEvent2dToPlayer(pRef, soundIndex, SoundCategory.SFX);
                }
            }
        }

        // Show arrival popup if waypoint has a message
        if (config.isShowArrivalPopup() && waypoint.getMessage() != null && !waypoint.getMessage().isEmpty()) {
            Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
            if (playerComponent != null) {
                PlayerRef pRef = store.getComponent(playerRef, PlayerRef.getComponentType());
                if (pRef != null) {
                    World world = store.getExternalData().getWorld();
                    WaypointArrivalPage page = new WaypointArrivalPage(
                            pRef, world, plugin,
                            waypoint, session);
                    playerComponent.getPageManager().openCustomPage(playerRef, store, page);
                }
            }
        }

        // Run waypoint arrival command if set (executed as console so route creator's intent works without player perms)
        String arrivalCmd = waypoint.getArrivalCommand();
        if (arrivalCmd != null && !arrivalCmd.isEmpty()) {
            Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
            if (playerComponent != null) {
                String cmd = substitutePlayerPlaceholder(arrivalCmd.trim(), playerComponent);
                if (cmd.startsWith("/")) {
                    cmd = cmd.substring(1);
                }
                if (!cmd.isEmpty()) {
                    CommandManager.get().handleCommand(ConsoleSender.INSTANCE, cmd);
                }
            }
        }

        // Advance to next waypoint
        if (session.advance()) {
            // Notify player of next waypoint
            Waypoint next = session.getCurrentWaypoint();
            if (next != null) {
                sendMessage(playerRef, store,
                        "Reached '" + waypoint.getName() + "'! Next: '" + next.getName() +
                                "' (" + session.getProgressString() + ")");
            }
        } else {
            // Route complete - run completion command if set (executed as console so route creator's intent works without player perms)
            String completionCommand = session.getRoute().getCompletionCommand();
            if (completionCommand != null && !completionCommand.isEmpty()) {
                Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
                if (playerComponent != null) {
                    String cmd = substitutePlayerPlaceholder(completionCommand.trim(), playerComponent);
                    if (cmd.startsWith("/")) {
                        cmd = cmd.substring(1);
                    }
                    if (!cmd.isEmpty()) {
                        CommandManager.get().handleCommand(ConsoleSender.INSTANCE, cmd);
                    }
                }
            }
            sendMessage(playerRef, store,
                    "Route '" + session.getRoute().getName() + "' completed!");
            plugin.getNavigationManager().stopNavigation(playerRef);
        }
    }

    private void spawnTrail(
            @Nonnull Ref<EntityStore> playerRef,
            @Nonnull Vector3d playerPos,
            @Nonnull Waypoint target,
            @Nonnull Store<EntityStore> store,
            @Nonnull WayfinderConfig config) {

        double dx = target.getX() - playerPos.x;
        double dy = target.getY() - playerPos.y;
        double dz = target.getZ() - playerPos.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance < 0.5) {
            return;
        }

        // Normalize direction
        double nx = dx / distance;
        double ny = dy / distance;
        double nz = dz / distance;

        double maxDist = Math.min(distance, config.getTrailMaxDistance());
        double spacing = config.getTrailSpacing();
        String particle = config.getDefaultParticle();

        // Target only this player
        List<Ref<EntityStore>> targetPlayers = Collections.singletonList(playerRef);

        World world = store.getExternalData().getWorld();

        for (double d = spacing; d <= maxDist; d += spacing) {
            double px = playerPos.x + nx * d;
            double py = playerPos.y;
            double pz = playerPos.z + nz * d;

            // Raycast downward to find ground level at this position
            if (world != null) {
                Vector3d groundPos = TargetUtil.getTargetLocation(
                        world, blockId -> blockId != 0,
                        px, py + 3.0, pz,
                        0.0, -1.0, 0.0, 10.0);
                if (groundPos != null) {
                    py = groundPos.y + 0.3;
                }
            }

            Vector3d particlePos = new Vector3d(px, py, pz);
            ParticleUtil.spawnParticleEffect(particle, particlePos, targetPlayers, store);
        }
    }

    private void sendMessage(@Nonnull Ref<EntityStore> playerRef, @Nonnull Store<EntityStore> store, @Nonnull String message) {
        Player player = store.getComponent(playerRef, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw(message));
        }
    }

    /** Replaces {player} in the command with the player's display name for console-executed commands. */
    private static String substitutePlayerPlaceholder(@Nonnull String command, @Nonnull Player player) {
        String name = player.getDisplayName();
        return name != null ? command.replace("{player}", name) : command;
    }
}
