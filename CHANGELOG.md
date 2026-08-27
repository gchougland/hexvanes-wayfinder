# Changelog

All notable changes to Hexvane's Wayfinder are documented here.

## [0.3.0]

### Hytale Update 6

- Target server version range is now `>=0.6.0-pre.0 <0.7.0`.
- Open commands (`start`, `stop`, `skip`, `list`, `info`, `preview`, and the parent `/wayfinder` command) now call `requireNoPermission()` instead of the removed `canGeneratePermission()` override.
- Position and particle APIs now use JOML `Vector3d`.
- Arrival messages and `{player}` command substitution now go through `PlayerRef`.
- Commands now declare typed arguments with tab completion for route names, waypoint indexes, and sound events.

## [0.2.0]

### Permissions

- **Permissions for certain commands have been changed to allow all players to use the command.** start, stop, skip, list, info, and preview have been changed to not require permissions. (Thanks to Jumento for the change)
