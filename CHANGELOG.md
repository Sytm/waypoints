# Changelog

## 4.0.0
### Breaking changes

- Support for Spigot has been dropped, [Paper](https://papermc.io/software/paper) is required
- Only latest Minecraft version is supported. At the moment 1.19.4
- Translation files are now formatted with [MiniMessage](https://docs.advntr.dev/minimessage/format.html) instead of legacy color codes
- Database format has been updated and is not backwards compatible

### New features

- Command suggestion tooltips are now customizable via the translation files at `command.search.tooltip`
- Update checker that notifies the console on startup and admins once after they first join. Can be disabled in the config at `general.updateChecker`
- Database accesses are now completely asynchronous to the main server thread
- Add support for [Folia](https://papermc.io/software/folia)
- Multiple Waypoints can be selected at the same time (#92)
- Integration to [Pl3xMap](https://modrinth.com/plugin/pl3xmap) has been added

### Misc

- Compass pointer nether support has been removed, because any possible implementation is inherently buggy
- Command help messages use used alias again