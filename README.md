# Waypoints

Ever had the problem, that you were on a long trip and don't find your base?

You could set a warp with a conventional plugin at your home and simply teleport back. But if you want to keep the survival experience of not being able to
teleport like that, this plugin could be just what your server needs!

## Direction Indicators

So, instead of just bringing the player back where he started, this plugin shows him the direction he has to walk, so he can explore the world even more. To
show the player the direction you can configure multiple direction indicators to guide the player to his destination.

### Compass

It won't get any simpler than that if the player owns a compass. It will have the waypoint as it's target and points into that direction.

### Action Bar Indicator

This is comparable to the compass but doesn't require one. It will show the rough direction with small sections in the action bar of the player. If the player
is too far left, the sections in the right will light up and vice versa.

### Beacon

When the waypoint is in render distance, a beacon beam will appear at the location of the waypoint to make it easier to pinpoint the location of the waypoint.

### Blinking Block

When the player is closer to the waypoint, the beacon will be replaced with a block sequence that will repeat. This is to help him get to the exact location.

### Hologram

As an alternative to the Blinking Block you can use Holograms to get the exact position of a Waypoint. This feature is optional but
requires [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) if wanted

### Particles

Particles appear at the feet of the player pointing into the direction of the waypoint

## Other notable features

- Open the GUI by sneaking and right-clicking with a compass (by default)
- Set custom icons for waypoints and folders by clicking on their icon. The item in your main hand will be used
- Limit the amount of waypoints and folders a player can have
- Customizable teleport prices and price increases the more the player teleports (With Vault support)
- Direction indicators customizable
- Items in GUI fully customizable
- Fully translatable
- SQLite data storage

## Commands

`/waypoints` - Opens the GUI

`/waypoints set <Name>` - Create a waypoint only the player himself can see

`/waypoints setPublic <Name>` - Create a waypoint visible for everyone

`/waypoints setPermission <Permission> <Name>` - Create a waypoint only visible for players with the given permission

`/waypoints other <Name|UUID>` - View the waypoints of another player

`/waypoints statistics` - Look at some rudimentary statistics about the database

`/waypoints import` - Import waypoints from Waypoints v2.X.X

### Scripting

`/waypointsscript deselectWaypoint <Player-Name>` - Deselect the current waypoint of a player with a command

`/waypointsscript selectWaypoint <Player-Name> <Waypoint-UUID>` - Set the waypoint selection of a player with a command

## Permissions

`waypoints.command.use` - Allows the player to use the `/waypoints` command and open the GUI

`waypoints.command.other` - Allows the player to use the `/waypoints other` command

`waypoints.command.statistics` - Allows the player to use the `/waypoints statistics` command

`waypoints.command.import` - Allows the player to use the `/waypoints import` command

`waypoints.command.scripting` - Allows the player to use the `/waypointsscript` command and get the UUID of waypoints

`waypoints.modify.private` - Allows the player to create, alter and delete private waypoints

`waypoints.modify.public` - Allows the player to create, alter and delete public waypoints

`waypoints.modify.permission` - Allows the player to create, alter, delete and always see permission waypoints

`waypoints.unlimited` - Allows the player to create more waypoints and folders than the limit in the config

`waypoints.teleport.private` - Allows the player to teleport to private waypoints

`waypoints.teleport.public` - Allows the player to teleport to public waypoints

`waypoints.teleport.permission` - Allows the player to teleport to permission waypoints

## Configuration

Almost everything is configurable. To have a look at the current configuration file
click [here](https://github.com/Sytm/waypoints/blob/v3/master/waypoints/src/main/resources/config.yml)

Also, you can change every piece of text to other languages or just change the colors.
[English Translation File](https://github.com/Sytm/waypoints/blob/v3/master/waypoints/src/main/resources/lang/en.yml)

## Additional notes

### Migrating from v2.X.X

- Shutdown the server
- Make backups of
  - `plugins/Waypoints/`
  - `plugins/Md5Lukas-Commons/`
- Delete
  - The old plugin jar (for example `waypoints-2.9.2.jar`)
  - `plugins/Waypoints/config.yml`
  - `plugins/Waypoints/config.base.yml`
  - `plugins/Waypoints/lang/`
  - md5-commons (for example `md5-commons-1.2-SNAPSHOT.jar`)
- Put the new plugin jar into place
- Start the server
- Run `/waypoints import` **ONLY ONCE**
- Make sure the old waypoints are all there
- Delete
  - `plugins/Waypoints/globalstore.nbt`
  - `plugins/Md5Lukas-Commons/`

### Metrics

This plugin uses [bStats](https://bstats.org/) to collect basic metrics about the usage of this plugin. You can, of course, opt-out of this if you wish. For
further information have a look at "What data is collected?"
and "Disabling bStats" over [here](https://bstats.org/getting-started).

To view the metrics of this plugin, go [here](https://bstats.org/plugin/bukkit/waypoints2/6864).

### License

This plugin is licensed under the MIT license. For more information on it, you can read [this](https://choosealicense.com/licenses/mit/).

### Supported Minecraft versions

Because I don't have the time or motivation to support a lot of versions, and it is generally advised to update to the latest Minecraft/Spigot versions only the
last 3 major updates with the latest builds are supported.

At the time of writing this would be 1.18.1, 1.17.1 and 1.16.5, but not 1.18, 1.16.4 or 1.15.2. 