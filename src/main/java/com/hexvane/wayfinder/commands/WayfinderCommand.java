package com.hexvane.wayfinder.commands;

import com.hexvane.wayfinder.WayfinderPlugin;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import javax.annotation.Nonnull;

/**
 * Parent command for all Wayfinder subcommands.
 * Usage: /wayfinder [subcommand]
 */
public class WayfinderCommand extends AbstractCommandCollection {
    public WayfinderCommand(@Nonnull WayfinderPlugin plugin) {
        super("wayfinder", "Wayfinder navigation commands");
        this.addAliases("wf");
        this.addSubCommand(new WayfinderStartCommand(plugin));
        this.addSubCommand(new WayfinderStopCommand(plugin));
        this.addSubCommand(new WayfinderSkipCommand(plugin));
        this.addSubCommand(new WayfinderCreateCommand(plugin));
        this.addSubCommand(new WayfinderDeleteCommand(plugin));
        this.addSubCommand(new WayfinderAddWaypointCommand(plugin));
        this.addSubCommand(new WayfinderRemoveWPCommand(plugin));
        this.addSubCommand(new WayfinderSetWaypointCommand(plugin));
        this.addSubCommand(new WayfinderSetMessageCommand(plugin));
        this.addSubCommand(new WayfinderSetRadiusCommand(plugin));
        this.addSubCommand(new WayfinderSetArrivalCommand(plugin));
        this.addSubCommand(new WayfinderSetCompletionCommand(plugin));
        this.addSubCommand(new WayfinderSetSoundCommand(plugin));
        this.addSubCommand(new WayfinderListCommand(plugin));
        this.addSubCommand(new WayfinderInfoCommand(plugin));
        this.addSubCommand(new WayfinderPreviewCommand(plugin));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}
