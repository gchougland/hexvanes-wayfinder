# Hexvane's Wayfinder

An in-world navigation mod for Hytale. Create routes as ordered waypoints; players follow a particle trail and compass marker to each waypoint. Optional arrival messages, sounds, and server commands when waypoints or the full route are completed.

## Features

- **Particle trails** – A trail leads from the player toward the current waypoint (configurable distance).
- **Compass markers** – The active waypoint appears on the world map.
- **Arrival radius** – When the player enters a waypoint's radius (default 3 blocks), the next waypoint activates.
- **Arrival popups** – Optional message and sound when a waypoint is reached.
- **Commands** – Run server commands when a waypoint is reached or when the route is completed (runs as console; use `{player}` for the player's name).
- **Routes persist** – Saved as JSON in the plugin data directory and survive server restarts.

## Installation

1. Download the latest release.
2. Place the JAR in your Hytale server's `plugins` directory.
3. Restart the server.

## Quick Start

Use `/wayfinder` or `/wf`. Use underscores or dashes in route and waypoint names so arguments are clear (e.g. `My_First_Route`, `Starting_Area`).

```
/wf create My_First_Route
/wf addwaypoint My_First_Route Starting_Area
/wf addwaypoint My_First_Route The_Old_Bridge
/wf setmessage My_First_Route 2 Cross the bridge and look for the hidden cave entrance.
/wf start My_First_Route
```

A particle trail guides to waypoint 1; when you get close enough, it advances to waypoint 2 and shows the message if set.

## Commands

| Command | Description |
|---------|-------------|
| `/wf create <name>` | Create a new route |
| `/wf delete <name>` | Delete a route |
| `/wf list` | List all routes and waypoint counts |
| `/wf info <name>` | Show route details (waypoints, positions, radii) |
| `/wf addwaypoint <route> [name]` | Add waypoint at your position |
| `/wf removewaypoint <route> <index>` | Remove a waypoint (indices are 1-based) |
| `/wf setwaypoint <route> <index>` | Move waypoint to your current position |
| `/wf setmessage <route> <index> <message>` | Set arrival popup message |
| `/wf setradius <route> <index> <radius>` | Set arrival radius in blocks (default 3.0) |
| `/wf setarrivalcommand <route> <index> [command]` | Run command when waypoint reached (console; use `{player}`) |
| `/wf setsound <route> <index> [soundEventId]` | Override arrival sound |
| `/wf setcompletioncommand <route> [command]` | Run command when route completed (console; use `{player}`) |
| `/wf start <route>` | Begin navigating a route |
| `/wf stop` | Stop current navigation |
| `/wf skip` | Skip to next waypoint |
| `/wf preview <route>` | Spawn particles at all waypoints to preview the route |

## Configuration

Config file: `wayfinder_config.json` in the plugin data directory. Options include `defaultParticle`, `trailSpacing`, `trailMaxDistance`, `defaultArrivalRadius`, `visualMode` (FLOATING or GROUNDED), `tickRate`, `showArrivalPopup`, and `allowPlayerDisable`.

Routes are stored as JSON files under `routes/` in the same directory.

## Documentation

See [USAGE_GUIDE.md](USAGE_GUIDE.md) for step-by-step route building, fixing mistakes, command triggers, and full configuration details.

## Support

Join [Hexvane's Mods Discord](https://discord.gg/5ywTFRk8Ft) for support, questions, or suggestions.

## License

MIT License. See [LICENSE](LICENSE) for details.
