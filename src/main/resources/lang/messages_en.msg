variable.prefix=&b&lWaypoints &e&l>&r

# General

general.notAPlayer=$prefix$ &cYou must be a player to use this command.
general.noPermission=$prefix$ &cYou don't have permissions to do this.
general.waypointNotFound=$prefix$ &cA waypoint with that id could not be found.
general.folderNotFound=$prefix$ &cA folder with that id could not be found.
general.notAValidUuid=$prefix$ &cThe id provided is not in a valid format.

# %currentworld% = Name of the world, the player is in
# %correctworld% = Name of the world, where the waypoint is
display.wrongWorld=&cYou are currently in the world &a%currentworld%&c, but the waypoint is in &a%correctworld%

chat.duration.second=second
chat.duration.seconds=seconds
chat.duration.minute=minute
chat.duration.minutes=minutes
chat.duration.hour=hour
chat.duration.hours=hours
chat.duration.day=day
chat.duration.days=days

variable.chatAction.updateItem.waypoint=&7Click here when you are holding the item you want to use as the new waypoint icon.

chatAction.updateItem.waypoint.private=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.waypoint.public=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.waypoint.permission=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.folder.private=$prefix$ &7Click here when you are holding the item you want to use as the new folder icon.

chatAction.rename.waypoint.private=$prefix$ &7Click here to get the command to rename your waypoint.
chatAction.rename.waypoint.public=$prefix$ &7Click here to get the command to rename the public waypoint.
chatAction.rename.waypoint.permission=$prefix$ &7Click here to get the command to rename the permission waypoint.
chatAction.rename.folder.private=$prefix$ &7Click here to get the command to rename your folder.

chatWarning.worldTranslationsMissing=$prefix$ &cSome world name translations are missing.

# %remainingTime% the time that is left until the player can teleport again
chat.teleport.onCooldown=$prefix$ &cYou cannot teleport right now because it is on cooldown. You can teleport again in &e%remainingTime%&c.
chat.teleport.notEnough.xpPoints=$prefix$ &cYou do not have enough xp points to perform this teleportation.
chat.teleport.notEnough.xpLevels=$prefix$ &cYou do not have enough xp levels to perform this teleportation.
chat.teleport.notEnough.balance=$prefix$ &cYou do not have enough balance to perform this teleportation.
# %timeRequired% the time the player is required to stand still
chat.teleport.standStillNotice=$prefix$ &7You need to stand still for &e%timeRequired%&7 to be able to teleport.
chat.teleport.cancelledMove=$prefix$ &cYour teleportation has been cancelled because you moved.

# Commands
command.help.title=$prefix$ &7&nHelp
command.help.help=&7 - &e/waypoints &7&l|&r&7 Show this help
command.help.set.private=&7 - &e/waypoints set <Name> &7&l|&r&7 Create a new waypoint
command.help.set.public=&7 - &e/waypoints setPublic <Name> &7&l|&r&7 Create a new public waypoint
command.help.set.permission=&7 - &e/waypoints setPermission <Permission> <Name> &7&l|&r&7 Create a new permission waypoint
command.help.compass=&7 - &e/waypoints compass &7&l|&r&7 Sets the new default compass target, when set to "ingame"
command.help.other=&7 - &e/waypoints other <Name|UUID> &7&l|&r&7 View the waypoints of another player
command.help.updateItem=&7 - &e/waypoints updateItem <Waypoint|Folder> <ID> &7&l|&r&7 This command is used to update the icons of waypoints and folders
command.help.rename.normal=&7 - &e/waypoints rename <PrivateWaypoint|PublicWaypoint|PermissionWaypoint|Folder> <ID> &7&l|&r&7 This command is used to rename waypoints or folders
command.help.rename.waypointOnly=&7 - &e/waypoints rename <PrivateWaypoint|PublicWaypoint|PermissionWaypoint> <ID> &7&l|&r&7 This command is used to rename waypoints
command.help.rename.folderOnly=&7 - &e/waypoints rename <Folder> <ID> &7&l|&r&7 This command is used to rename folders

command.compass.disabled=$prefix$ &cThis method to set the default compass target has not been enabled in the config.
command.compass.locked=$prefix$ &cThis command has been disabled in the config.
command.compass.setSuccess=$prefix$ &7The new default compass target has been set.

command.set.private.wrongUsage|
|$prefix$ &cYou need to provide a name to create a new waypoint. Example:
|&a/waypoints set My Home
command.set.private.nameDuplicate=$prefix$ &cWaypoints with duplicate names have been disabled.
command.set.private.limitReached=$prefix$ &cYou have reached your personal limit of waypoints you can set.
command.set.private.success=$prefix$ &7The waypoint has been set successfully.

command.set.public.wrongUsage|
|$prefix$ &cYou need to provide a name to create a new public waypoint. Example:
|&a/waypoints setPublic Spawn
command.set.public.nameDuplicate=$prefix$ &cPublic waypoints with duplicate names have been disabled.
command.set.public.success=$prefix$ &7The public waypoint has been set successfully.

command.set.permission.wrongUsage|
|$prefix$ &cYou need to provide a permission and name to create a new permission waypoint. Example:
|&a/waypoints setPermission ranks.vip.secretHideout Secret Hideout
command.set.permission.nameDuplicate=$prefix$ &cPermission waypoints with duplicate names have been disabled.
command.set.permission.success=$prefix$ &7Permission waypoint has been set successfully.

command.createFolder.wrongUsage|
|$prefix$ &cYou need to provide a name to create a new folder. Example:
|&a/waypoints createFolder Nether portals
command.createFolder.nameDuplicate=$prefix$ &cFolders with duplicate names have been disabled.
command.createFolder.limitReached=$prefix$ &cYou have reached your personal limit of folders you can create.
command.createFolder.success=$prefix$ &7Folder created successfully.

command.other.wrongUsage|
|$prefix$ &cTo view the waypoints of another player you need to provide their name or uuid. Example:
|&a/waypoints other Sytm
|&a/waypoints other 68f22021-732e-4afe-9cf5-6906f07a41db
command.other.notAValidUuidOrPlayerName=$prefix$ &cThe provided identifier is neither a Minecraft username nor a valid uuid.
command.other.uuidNotFound=$prefix$ &cA player with that uuid could not be found.
command.other.playerNameNotFound=$prefix$ &cA player with that name could not be found.

command.updateItem.disabled=$prefix$ &cUpdating items has been disabled.
command.updateItem.notAValidItem=$prefix$ &cThe item you are trying to use is not allowed.
command.updateItem.wrongUsage|
|$prefix$ &cTo update the item you need to specify whether its a folder or waypoint and provide the id of it. Example:
|&a/waypoints updateItem waypoint 729dad02-6115-46a9-a2a3-ab6cb2c36c35
|&a/waypoints updateItem folder 59bcef04-9d5f-4a05-8fa4-f1f7b5311870
command.updateItem.waypoint.success=$prefix$ &7The item of the waypoint has been updated successfully.
command.updateItem.folder.success=$prefix$ &7The item of the folder has been updated successfully.

command.rename.disabled=$prefix$ &cRenaming has been disabled.
command.rename.viaCmdDisabled=$prefix$ &cRenaming waypoints with the command is disabled, please use the gui.
command.rename.waypoint.private.disabled=$prefix$ &cRenaming private waypoints has been disabled.
command.rename.waypoint.public.disabled=$prefix$ &cRenaming public waypoints has been disabled.
command.rename.waypoint.permission.disabled=$prefix$ &cRenaming permission waypoints has been disabled.
command.rename.folder.disabled=$prefix$ &cRenaming folders has been disabled.
command.rename.wrongUsage|
|$prefix$ &cYou need to provide a new name and the type of what you want to rename. Example:
|&a/waypoints rename privateWaypoint 729dad02-6115-46a9-a2a3-ab6cb2c36c35 Nether Portal No. 2
|&a/waypoints updateItem folder 59bcef04-9d5f-4a05-8fa4-f1f7b5311870 Mines
command.rename.waypoint.private.nameDuplicate=$prefix$ &cDuplicate names for waypoints have been disabled.
command.rename.waypoint.public.nameDuplicate=$prefix$ &cDuplicate names for public waypoints have been disabled.
command.rename.waypoint.permission.nameDuplicate=$prefix$ &cDuplicate names for permission waypoints have been disabled.
command.rename.waypoint.success=$prefix$ &7The waypoint has been renamed successfully.
command.rename.folder.nameDuplicate=$prefix$ &cDuplicate names for folders have been disabled.
command.rename.folder.success=$prefix$ &7The folder has been renamed successfully.

command.notFound=$prefix$ &cThe command you entered was not found


# Inventory
variable.empty=&0-
variable.waypoints.select.displayName=&eSelect waypoint
variable.waypoints.select.description=&7Click here to make this waypoint your active one
variable.waypoints.delete.displayName=&cDelete the waypoint
variable.waypoints.delete.description=&0-
variable.waypoints.rename.displayName=&eRename waypoint
variable.waypoints.rename.description=&7Click here to receive a message, which you can click to receive the rename command
variable.waypoints.teleport.displayName=&eTeleport to waypoint
variable.waypoints.teleport.description=&0-
variable.waypoints.teleport.payment.xpPoints.description=&7You need &e%points%&7 experience points to perform this teleportation
variable.waypoints.teleport.payment.xpLevels.description=&7You need &e%levels%&7 experience levels to perform this teleportation
variable.waypoints.teleport.payment.vault.description=&7You need &e%money%&7€ to perform this teleportation

inventory.title.own=&eWaypoints
# %name% = Other players name
inventory.title.other=&6%name%'s &ewaypoints

inventory.general.previous.displayName=&ePrevious page
inventory.general.previous.description=$empty$
inventory.general.next.displayName=&eNext page
inventory.general.next.description=$empty$
inventory.general.back.displayName=&eGo back
inventory.general.back.description=$empty$

inventory.cycleSort.displayName=&eSort mode
inventory.cycleSort.description=&eClick here to cycle through the different sort modes
# %name% = Name of the sort mode
inventory.cycleSort.active=&6%name%
# %name% = Name of the sort mode
inventory.cycleSort.inactive=&8%name%
inventory.cycleSort.mode.name.ascending=Name (Ascending)
inventory.cycleSort.mode.name.descending=Name (Descending) 
inventory.cycleSort.mode.createdAt.ascending=Created (Ascending)
inventory.cycleSort.mode.createdAt.descending=Created (Descending)
inventory.cycleSort.mode.type=Type

inventory.overview.background.displayName=$empty$
inventory.overview.background.description=$empty$
inventory.overview.deselect.displayName=&eDeselect active waypoint
inventory.overview.deselect.description=&7Click here, when you have reached the waypoint and don't need the direction indicators anymore
inventory.overview.toggleGlobals.shown.displayName=&eGlobal waypoints are visible
inventory.overview.toggleGlobals.shown.description=&7Click here to hide them
inventory.overview.toggleGlobals.hidden.displayName=&eGlobal waypoints are hidden
inventory.overview.toggleGlobals.hidden.description=&7Click here to show them
inventory.overview.setWaypoint.displayName=&eSet new waypoints
inventory.overview.setWaypoint.description=$empty$
inventory.overview.createFolder.displayName=&eCreate new folder
inventory.overview.createFolder.description=$empty$

inventory.selectWaypointType.background.displayName=$empty$
inventory.selectWaypointType.background.description=$empty$
inventory.selectWaypointType.title.displayName=What kind of waypoint would you like to set?
inventory.selectWaypointType.title.description=$empty$
inventory.selectWaypointType.private.displayName=&eA private Waypoint
inventory.selectWaypointType.private.description=$empty$
inventory.selectWaypointType.public.displayName=&eA public Waypoint
inventory.selectWaypointType.public.description=$empty$
inventory.selectWaypointType.permission.displayName=&eA permission Waypoint
inventory.selectWaypointType.permission.description=$empty$

inventory.waypoint.distance.otherWorld=?

inventory.waypoint.death.displayName=&cDeath-Waypoint
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.death.description|
|&7World: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distance: &6%distance%
inventory.waypoint.death.background.displayName=$empty$
inventory.waypoint.death.background.description=$empty$
inventory.waypoint.death.select.displayName=$waypoints.select.displayName$
inventory.waypoint.death.select.description=$waypoints.select.description$
inventory.waypoint.death.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.death.teleport.description=$waypoints.teleport.description$
inventory.waypoint.death.teleport.payment.xpPoints.description=$waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.death.teleport.payment.xpLevels.description=$waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.death.teleport.payment.vault.description=$waypoints.teleport.payment.vault.description$

# %name% = Name of the waypoint
inventory.waypoint.private.displayName=&5%name%
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.private.description|
|&7World: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distance: &6%distance%
inventory.waypoint.private.background.displayName=$empty$
inventory.waypoint.private.background.description=$empty$
inventory.waypoint.private.select.displayName=$waypoints.select.displayName$
inventory.waypoint.private.select.description=$waypoints.select.description$
inventory.waypoint.private.delete.displayName=$waypoints.delete.displayName$
inventory.waypoint.private.delete.description=$waypoints.delete.description$
inventory.waypoint.private.rename.displayName=$waypoints.rename.displayName$
inventory.waypoint.private.rename.description=$waypoints.rename.description$
inventory.waypoint.private.moveToFolder.displayName=&eMove waypoint into a folder
inventory.waypoint.private.moveToFolder.description=&7Put the waypoint into a folder to group it, or remove it from the current folder
inventory.waypoint.private.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.private.teleport.description=$waypoints.teleport.description$
inventory.waypoint.private.teleport.payment.xpPoints.description=$waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.private.teleport.payment.xpLevels.description=$waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.private.teleport.payment.vault.description=$waypoints.teleport.payment.vault.description$

# %name% = Name of the waypoint
inventory.waypoint.public.displayName=&b%name%
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.public.description|
|&7World: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distance: &6%distance%
inventory.waypoint.public.background.displayName=$empty$
inventory.waypoint.public.background.description=$empty$
inventory.waypoint.public.select.displayName=$waypoints.select.displayName$
inventory.waypoint.public.select.description=$waypoints.select.description$
inventory.waypoint.public.delete.displayName=$waypoints.delete.displayName$
inventory.waypoint.public.delete.description=$waypoints.delete.description$
inventory.waypoint.public.rename.displayName=$waypoints.rename.displayName$
inventory.waypoint.public.rename.description=$waypoints.rename.description$
inventory.waypoint.public.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.public.teleport.description=$waypoints.teleport.description$
inventory.waypoint.public.teleport.payment.xpPoints.description=$waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.public.teleport.payment.xpLevels.description=$waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.public.teleport.payment.vault.description=$waypoints.teleport.payment.vault.description$

# %name% = Name of the waypoint
inventory.waypoint.permission.displayName=&d%name%
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.permission.description|
|&7World: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distance: &6%distance%
inventory.waypoint.permission.background.displayName=$empty$
inventory.waypoint.permission.background.description=$empty$
inventory.waypoint.permission.select.displayName=$waypoints.select.displayName$
inventory.waypoint.permission.select.description=$waypoints.select.description$
inventory.waypoint.permission.delete.displayName=$waypoints.delete.displayName$
inventory.waypoint.permission.delete.description=$waypoints.delete.description$
inventory.waypoint.permission.rename.displayName=$waypoints.rename.displayName$
inventory.waypoint.permission.rename.description=$waypoints.rename.description$
inventory.waypoint.permission.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.permission.teleport.description=$waypoints.teleport.description$
inventory.waypoint.permission.teleport.payment.xpPoints.description=$waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.permission.teleport.payment.xpLevels.description=$waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.permission.teleport.payment.vault.description=$waypoints.teleport.payment.vault.description$

inventory.selectFolder.background.displayName=$empty$
inventory.selectFolder.background.description=$empty$
inventory.selectFolder.noFolder.displayName=&eNo folder
inventory.selectFolder.noFolder.description=&7Move this waypoint back to the main overview

# %name% = Folder name
inventory.folder.private.displayName=&b%name%
# %amount% = Amount of waypoints in folder
inventory.folder.private.description=&7Waypoints in this folder: &6%amount%
inventory.folder.private.background.displayName=$empty$
inventory.folder.private.background.description=$empty$
inventory.folder.private.delete.displayName=&4Delete folder
inventory.folder.private.delete.description=$empty$
inventory.folder.private.rename.displayName=&eRename folder
inventory.folder.private.rename.description=&7Click here to receive a message, which you can click to receive the rename command

inventory.folder.public.displayName=&bGlobal waypoints
# %amount% = Amount of waypoints in folder
inventory.folder.public.description|
|&7Waypoints in here are available to everyone
|
|&7Currently &6%amount% &7waypoints are available
inventory.folder.public.background.displayName=$empty$
inventory.folder.public.background.description=$empty$

inventory.folder.permission.displayName=&dPermission waypoints
# %amount% = Amount of waypoints in folder
inventory.folder.permission.description|
|&7Waypoints in here are only available to you,
|&7if you have certain permissions
|
|&6%amount% &7waypoints are available for you
inventory.folder.permission.background.displayName=$empty$
inventory.folder.permission.background.description=$empty$

inventory.confirmMenu.background.displayName=$empty$
inventory.confirmMenu.background.description=$empty$

inventory.confirmMenu.waypoint.private.delete.title.displayName=&7Are you sure you want to delete your waypoint?
inventory.confirmMenu.waypoint.private.delete.title.description=$empty$
inventory.confirmMenu.waypoint.private.delete.yes.displayName=&7&lYes&7, delete waypoint
inventory.confirmMenu.waypoint.private.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.private.delete.no.displayName=&7&lNo&7, don't delete waypoint
inventory.confirmMenu.waypoint.private.delete.no.description=$empty$

inventory.confirmMenu.waypoint.public.delete.title.displayName=&7Are you sure you want to delete this public waypoint?
inventory.confirmMenu.waypoint.public.delete.title.description=$empty$
inventory.confirmMenu.waypoint.public.delete.yes.displayName=&7&lYes&7, delete waypoint
inventory.confirmMenu.waypoint.public.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.public.delete.no.displayName=&7&lNo&7, don't delete waypoint
inventory.confirmMenu.waypoint.public.delete.no.description=$empty$

inventory.confirmMenu.waypoint.permission.delete.title.displayName=&7Are you sure want to delete this permission waypoint?
inventory.confirmMenu.waypoint.permission.delete.title.description=$empty$
inventory.confirmMenu.waypoint.permission.delete.yes.displayName=&7&lYes&7, delete waypoint
inventory.confirmMenu.waypoint.permission.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.permission.delete.no.displayName=&7&lNo&7, don't delete waypoint
inventory.confirmMenu.waypoint.permission.delete.no.description=$empty$

inventory.confirmMenu.folder.private.delete.title.displayName=&7Are you sure you want to delete your folder?
inventory.confirmMenu.folder.private.delete.title.description=$empty$
inventory.confirmMenu.folder.private.delete.yes.displayName=&7&lYes&7, delete folder
inventory.confirmMenu.folder.private.delete.yes.description=$empty$
inventory.confirmMenu.folder.private.delete.no.displayName=&7&lNo&7, don't delete waypoint
inventory.confirmMenu.folder.private.delete.no.description=$empty$

inventory.anvilGUI.enterNameHere=Enter name here
inventory.anvilGUI.enterPermissionHere=Enter permission here