# Hexvane's Wayfinder - Usage Guide

Wayfinder lets you create particle trail routes that guide players between waypoints. Players see a trail of particles leading to the next waypoint, a compass marker on the world map, and an optional arrival popup with a message.

All commands use `/wayfinder` (or the shorthand `/wf`). Use underscores or dashes in route and waypoint names (e.g. `My_First_Route`, `Starting_Area`) so it is clear where the route name ends and other arguments begin.

## Quick Start

1. Create a route:
   ```
   /wf create My_First_Route
   ```

2. Walk to your first location and add a waypoint:
   ```
   /wf addwaypoint My_First_Route Starting_Area
   ```

3. Walk to the next location and add another:
   ```
   /wf addwaypoint My_First_Route The_Old_Bridge
   ```

4. Optionally set an arrival message on a waypoint:
   ```
   /wf setmessage My_First_Route 2 Cross the bridge and look for the hidden cave entrance.
   ```

5. Start navigating the route:
   ```
   /wf start My_First_Route
   ```

A particle trail will appear guiding you toward waypoint 1. When you get close enough, it automatically advances to waypoint 2, shows the arrival message, and the trail updates.

## Commands Reference

### Route Management

| Command | Description |
|---------|-------------|
| `/wf create <name>` | Create a new empty route |
| `/wf delete <name>` | Delete a route permanently |
| `/wf list` | List all routes with waypoint counts |
| `/wf info <name>` | Show detailed route info (waypoints, positions, radii) |

### Waypoint Editing

All waypoint indices are **1-based** (first waypoint = 1, second = 2, etc.).

| Command | Description |
|---------|-------------|
| `/wf addwaypoint <route> [name]` | Add a waypoint at your current position. If no name is given, defaults to "Waypoint N" |
| `/wf removewaypoint <route> <index>` | Remove a waypoint by index |
| `/wf setwaypoint <route> <index>` | Move an existing waypoint to your current position |
| `/wf setmessage <route> <index> <message>` | Set the arrival popup message for a waypoint |
| `/wf setradius <route> <index> <radius>` | Set arrival detection radius in blocks (default: 3.0) |
| `/wf setarrivalcommand <route> <index> [command]` | Run a command when the player reaches this waypoint. Runs as **console** (no player perms needed). Use `{player}` for the player's name, e.g. `give {player} diamond 1`. Omit command to clear. |
| `/wf setsound <route> <index> [soundEventId]` | Override arrival sound for this waypoint (default: SFX_Creative_Play_Add_Mask). Use the game's sound event ID. Omit to clear. |
| `/wf setcompletioncommand <route> [command]` | Run a command when the player completes the entire route. Runs as **console** (no player perms needed). Use `{player}` for the player's name. Omit command to clear. |

### Navigation

| Command | Description |
|---------|-------------|
| `/wf start <route>` | Begin navigating a route from waypoint 1 |
| `/wf stop` | Stop your current navigation |
| `/wf skip` | Skip to the next waypoint |
| `/wf preview <route>` | Spawn particles at all waypoints for a quick visual overview |
| `/wf permission` | Print the exact permission node for Wayfinder (use this to get the string for `/perm`) |

## Building a Route - Step by Step

### 1. Plan your path

Walk the path you want players to follow. Note the key locations where you want waypoints (turns, landmarks, destinations).

### 2. Create the route

```
/wf create Village_Tour
```

### 3. Add waypoints in order

Walk to each location and add waypoints. The order you add them is the order players will follow.

```
/wf addwaypoint Village_Tour Town_Square
/wf addwaypoint Village_Tour Blacksmith
/wf addwaypoint Village_Tour Market
/wf addwaypoint Village_Tour Castle_Gate
```

### 4. Add arrival messages (optional)

Messages appear in a popup when a player reaches that waypoint.

```
/wf setmessage Village_Tour 1 Welcome to the village! Head toward the blacksmith.
/wf setmessage Village_Tour 4 You've arrived at the castle. Speak to the guard to enter.
```

### 5. Adjust radii if needed

The default arrival radius is 3 blocks. For large open areas you may want a bigger trigger zone.

```
/wf setradius Village_Tour 1 5.0
```

### 6. Verify with info and preview

```
/wf info Village_Tour
/wf preview Village_Tour
```

`info` shows all waypoints with coordinates, radii, and whether they have messages. `preview` spawns particles at every waypoint so you can visually check placement.

### 7. Test the route

```
/wf start Village_Tour
```

Walk the route. Use `/wf skip` to jump ahead if needed. Use `/wf stop` to end early.

## Fixing Mistakes

**Moved a waypoint to the wrong spot?** Stand at the correct position and:
```
/wf setwaypoint Village_Tour 3
```

**Need to remove a waypoint?**
```
/wf removewaypoint Village_Tour 2
```
Note: this shifts all later waypoints down (waypoint 3 becomes 2, etc.).

**Wrong arrival message?** Just set it again:
```
/wf setmessage Village_Tour 2 Updated message here.
```

## Command Triggers

You can run a command when a waypoint is reached or when the whole route is completed.

- **Waypoint arrival command** — Set with `/wf setarrivalcommand <route> <index> [command]`. The command runs when the player enters that waypoint's radius. Omit the command to clear: `/wf setarrivalcommand Village_Tour 2`.
- **Waypoint arrival sound** — A sound plays when the player reaches any waypoint. By default the game sound `SFX_Creative_Play_Add_Mask` is used. Override per waypoint with `/wf setsound <route> <index> [soundEventId]` (omit the ID to clear and use the default again).
- **Route completion command** — Set with `/wf setcompletioncommand <route> <command>`. The command runs when the player reaches the last waypoint. Clear with `/wf setcompletioncommand Village_Tour` (no command after the route name).

**Who runs the command?** Commands are **executed as the server console**, not as the player. That way the player does **not** need to be opped or have permission—only the person who created the route and set the command needs to be able to use Wayfinder. Use the placeholder **`{player}`** in the command; it is replaced with the display name of the player who reached the waypoint or completed the route. Example: to give that player an item, use `give {player} diamond 1` (not `give @p ...`, since the console has no "p"). The leading slash is optional in the stored command.

Examples:
```
/wf setarrivalcommand Village_Tour 2 give {player} minecraft:cookie 1
/wf setcompletioncommand Village_Tour give {player} diamond 2
```

## How Navigation Works

- A **particle trail** extends from the player toward the current waypoint (up to 20 blocks ahead by default)
- A **compass marker** appears on the world map pointing to the active waypoint
- When the player enters the waypoint's **arrival radius**, the next waypoint activates (and any arrival sound/command runs)
- If the waypoint has a **message**, an arrival popup appears with Continue/Stop Navigation buttons
- When the last waypoint is reached, the route completion command runs (if set) and navigation ends automatically

## Permissions

Hytale **auto-generates** a permission node for each command from the plugin manifest. The format is **`<group>.<pluginname>.command.wayfinder`** for the base and **`<group>.<pluginname>.command.wayfinder.<subcommand>`** for each subcommand (all lowercase). **Group** and **Plugin name** come from the manifest (Name is usually the Gradle project name, e.g. the project folder). Example: if Group is `hexvane` and Name is `HexvanesWayfinder`, the base is **`hexvane.hexvaneswayfinder.command.wayfinder`** and subcommands are **`hexvane.hexvaneswayfinder.command.wayfinder.start`**, **`hexvane.hexvaneswayfinder.command.wayfinder.create`**, etc.

**Granting permissions** is done through the server’s permission system (e.g. giving players or groups the nodes above). Hytale requires **both** the **base** permission and the **subcommand** permission. Granting only `.start` or `.stop` is not enough; the player must also have the base. So: **All commands:** grant **`<base>.*`**. **Only /wf start:** grant **`<base>`** and **`<base>.start`**. **Only /wf stop:** grant **`<base>`** and **`<base>.stop`**. Example for start and stop only: `/perm user add <player> hexvane.hexvaneswayfinder.command.wayfinder` then add `....command.wayfinder.start` and `....command.wayfinder.stop`. Replace the prefix with your server base (see below).

**To see the exact permissions on your server:** run **`/wf permission`**. It prints the base, start, and stop permission nodes and example `/perm` commands.

## Configuration

The config file is saved to the plugin data directory as `wayfinder_config.json`. Default values:

| Setting | Default | Description |
|---------|---------|-------------|
| `defaultParticle` | `Wayfinder_Trail` | Particle effect used for trails |
| `trailSpacing` | `2.0` | Distance between trail particles (blocks) |
| `trailMaxDistance` | `20.0` | Max trail render distance ahead of player |
| `defaultArrivalRadius` | `3.0` | Default waypoint trigger radius (blocks) |
| `visualMode` | `FLOATING` | Trail mode: `FLOATING` (eye level) or `GROUNDED` (on terrain) |
| `tickRate` | `10` | How often trails update (in ticks) |
| `showArrivalPopup` | `true` | Show popup UI on waypoint arrival |
| `allowPlayerDisable` | `true` | Show "Stop Navigation" button in arrival popup |

## Data Storage

Routes are saved as individual JSON files in the plugin data directory under `routes/`. Each file is named `<routename>.json`. These files persist across server restarts.
