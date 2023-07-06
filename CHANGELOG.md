# Changelog

## [Unreleased]

### Added
- Java interoperability helpers have been added to the API Waypoints provides to allow non-Kotlin plugins to use it 

## 4.0.0
A lot of new features but also a lot of internal changes that leverage new Paper APIs that aren't available on Spigot.
Before updating please make an update of your database to make sure nothing goes missing in case of unforeseen errors.
I have tested this plugin in the current state for quite some time and it worked without issues for me, but if you encounter any
please report them on the GitHub issue tracker or on the Discord server.

### Breaking changes

- Support for Spigot has been dropped, [Paper](https://papermc.io/software/paper) is required
- Only latest Minecraft version is officially supported, although the minimum Version is 1.19.4
- Translation files are now formatted with [MiniMessage](https://docs.advntr.dev/minimessage/format.html) instead of legacy color codes
- Database format has been updated and is not backwards compatible
- When using `/waypointsscript selectWaypoint` it is now mandatory to run `/waypointscript deselectWaypoint` just before if only one waypoint should be active at the same time

### Added

- Command suggestion tooltips are now customizable via the translation files at `command.search.tooltip`
- Update checker that notifies the console on startup and admins once after they first join. Can be disabled in the config at `general.updateChecker`. Does not run periodically
- Database accesses are now completely asynchronous to the main server thread
- Add support for [Folia](https://papermc.io/software/folia)
- Multiple Waypoints can be selected at the same time [#92](https://github.com/Sytm/waypoints/issues/92)
- Integration to [Pl3xMap](https://modrinth.com/plugin/pl3xmap) has been added
- Descriptions have been added to waypoints and folders. To change them [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) needs to be installed
- A Particle Trail pointer has been added
- Players can choose to disable some of the server-wide available pointers (For example only having the bossbar)
- Players can share private waypoints with other players
- Sounds have been added to the GUI and some actions. They can be customized in the config at `sounds`

### Changed
- Command help messages use used alias again
- The Hologram Pointer uses display entities instead of ArmorStands
- Added extra item in the waypoint gui page for changing the icon of waypoints
- Reloading has always been discouraged but is now explicitly unsupported

### Removed
- Compass pointer nether support has been removed, because any possible implementation is inherently inconsistent
