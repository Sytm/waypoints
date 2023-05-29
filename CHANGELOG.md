# Changelog

## 4.0.0
### Breaking changes

- Support for Spigot has been dropped, [Paper] is required
- Only latest Minecraft version is supported. At the moment 1.19.4
- Translation files are now formatted with [MiniMessage] instead of legacy color codes
- Database format has been updated and is not backwards compatible
- When using `/waypointsscript selectWaypoint` it is now mandatory to run `/waypointscript deselectWaypoint` just before if only one waypoint should be active at the same time

### New features

- Command suggestion tooltips are now customizable via the translation files at `command.search.tooltip`
- Update checker that notifies the console on startup and admins once after they first join. Can be disabled in the config at `general.updateChecker`
- Database accesses are now completely asynchronous to the main server thread
- Add support for [Folia]
- Multiple Waypoints can be selected at the same time (#92)
- Integration to [Pl3xMap] has been added
- Descriptions have been added to waypoints and folders. To change them [ProtocolLib] needs to be installed
- A Particle Trail pointer has been added
- Players can choose to disable some of the server-wide available pointers (For example only having the bossbar)
- Players can share private waypoints with other players

### Misc

- Compass pointer nether support has been removed, because any possible implementation is inherently inconsistent
- Command help messages use used alias again
- The Hologram Pointer uses display entities instead of ArmorStands
- Added extra item in the waypoint gui page for changing the icon of waypoints

[Paper]: https://papermc.io/software/paper
[Folia]: https://papermc.io/software/folia
[MiniMessage]: https://docs.advntr.dev/minimessage/format.html
[ProtocolLib]: https://github.com/dmulloy2/ProtocolLib
[Pl3xMap]: https://modrinth.com/plugin/pl3xmap
