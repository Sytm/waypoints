## 2.9.2

### Added
- Support for Minecraft 1.16.4

### Fixed
- Trying to calculate the distance between two locations that may not be in the same world [#41](https://github.com/Sytm/waypoints/issues/41)

## 2.9.1

### Added
- Waypoints are automatically deselected when reached

## 2.9.0

### Added
- `/waypointsscript` command for easier automation of the plugin
- Players that no longer have the permission for a waypoint get the waypoint deselected

## 2.8.5

### Added
- Support for Minecraft 1.16.3

## 2.8.4

### Added
- Russian translations by darksumrak

## 2.8.3

### Added
- Support for Minecraft 1.16.2

## 2.8.2

### Added
- Support for Minecraft 1.16.1

## 2.8.1

### Fixed
- NPE when using required items for usage and players are joining

## 2.8.0

### Added
- Players can teleport to waypoint only if they have a permission, if they pay or for free, depending on configuration [#9](https://github.com/Sytm/waypoints/issues/9)

## 2.7.0

### Added
- bStats betrics
- Pointers only work if the player is in possesion of a specific item [#11](https://github.com/Sytm/waypoints/issues/11)

### Changed
- `config.base.yml` is always updated to the latest config state and `config.yml` can be used to actually configure the plugin

## 2.6.2

### Fixed
- Inventory items can get lost using AnvilGUI [#12](https://github.com/Sytm/waypoints/issues/12)

## 2.6.1

### Fixed
- Error message in the console when a player leavesthe server without having a waypoint selected

## 2.6

### Added
- Option to change the color of the beacon beam on a per waypoint basis
- Possibility to change the display items of public and permission waypoints

### Fixed
- Translation bug that caused chat message for updating the item of a private waypoint being the same as that for a private folder
- Old beacon and blinking block not disappearing when switching waypoints

## 2.5

### Added
- Support for Minecraft 1.15.2

## 2.4

### Fixed
- 100% CPU utilisation bug

## 2.3

### Fixed
- Some display methods stop working if another player selects them

## 2.2

### Added
- Option to use an AnvilGUI to create and rename waypoints and folders

## 2.1

### Added
- German translations

## 2.0

### Changed
- Complete rewrite of the plugin

## 1.6

### Fixed
- New waypoints cannot be added in Minecraft 1.14

## 1.5

### Fixed
- Error preventing the plugin from loading

## 1.4 (?)

### Added
- Normal players can now use the basic command without any extra adding of permissions

### Fixed
- Can't use command even when OP

## 1.3 (?)

### Fixed
- Console error spam when selected waypoint is in another world

## 1.2 (?)

### Added
- When crouching the player sees the distance and y-offset to the waypoint

## 1.1 (?)

### Added
- Tab-completions for the subcommands

## 1.1 / 1.05 (?)

### Added
- Global waypoints everyone has access to
- Permission waypoints only players with set permissions have access to
- Managing of others waypoints so admins can view and delete them
- Possibility to instantly teleport to a waypoint
- World name aliases in the config to make it look fancier in the waypoint overview (e.g. `world` -> `Survival World`)
- Auto saving of waypoints

### Changed
- Message file parser allows variables and better looking multiline text
- Optimized loading and saving of player waypoints to have a lower impact on ram usage

### Fixed
- Cannot open GUI if the player is in another world

## 1.0