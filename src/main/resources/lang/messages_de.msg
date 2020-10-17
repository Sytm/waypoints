variable.prefix=&b&lWaypoints &e&l>&r

# General

general.notAPlayer=$prefix$ &cDu musst ein Spieler sein, um diesen Befehl verwenden zu können
general.noPermission=$prefix$ &cDu hast nicht die benötigte Berechtigung, um dies zu tun.
general.waypointNotFound=$prefix$ &cEin Wegpunkt mit dieser ID konnte nicht gefunden werden.
general.folderNotFound=$prefix$ &cEin Ordner mit dieser ID konnte nicht gefunden werden.
general.notAValidUuid=$prefix$ &cDie angegebene ID ist in einem nicht gültigen Format.

# %currentworld% = Name of the world, the player is in
# %correctworld% = Name of the world, where the waypoint is
display.wrongWorld=&cDu bist momentan in der Welt &a%currentworld%&c, aber der Wegpunkt ist in &a%correctworld%

chat.duration.second=Sekunde
chat.duration.seconds=Sekunden
chat.duration.minute=Minute
chat.duration.minutes=Minuten
chat.duration.hour=Stunde
chat.duration.hours=Stunden
chat.duration.day=Tag
chat.duration.days=Tage

variable.chatAction.updateItem.waypoint=&7Drücke hier drauf, wenn du das Item in der Hand am halten bist, das du als Icon für den Wegpunkt verwenden möchtest.

chatAction.updateItem.waypoint.private=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.waypoint.public=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.waypoint.permission=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.folder.private=$prefix$ &7Drücke hier drauf, wenn du das Item in der Hand am halten bist, das du als Icon für den Ordner verwenden möchtest.

chatAction.rename.waypoint.private=$prefix$ &7Drücke hier drauf, um den Befehl zu erhalten, deinen Wegpunkt umzubenennen.
chatAction.rename.waypoint.public=$prefix$ &7Drücke hier drauf, um den Befehl zu erhalten, den öffentlichen Wegpunkt umzubenennen.
chatAction.rename.waypoint.permission=$prefix$ &7Drücke hier drauf, um den Befehl zu erhalten, den eingeschränkten Wegpunkt umzubenennen.
chatAction.rename.folder.private=$prefix$ &7Drücke hier drauf, um den Befehl zu erhalten, deinen Ordner umzubenennen.

chatAction.getId=$prefix$ &7Drücke hier drauf um die ID zu erhalten

chatWarning.worldTranslationsMissing=$prefix$ &cEs fehlen Übersetzungen für Welt-Namen.

# %remainingTime% the time that is left until the player can teleport again
chat.teleport.onCooldown=$prefix$ &cDu kannst im Moment dich nicht teleportieren. Dies ist erst in &e%remainingTime%&c möglich.
chat.teleport.notEnough.xpPoints=$prefix$ &cDu hast nicht genügend Erfahrungspunkte um diese teleportation durchzuführen.
chat.teleport.notEnough.xpLevels=$prefix$ &cDu hast nicht genügend Erfahrungs-Level um diese teleportation durchzuführen.
chat.teleport.notEnough.balance=$prefix$ &cDu hast nicht genügend Guthaben um diese teleportation durchzuführen.
# %timeRequired% the time the player is required to stand still
chat.teleport.standStillNotice=$prefix$ &7Du musst für &e%timeRequired%&7 stehen bleiben, um dich teleportieren zu können.
chat.teleport.cancelledMove=$prefix$ &cDie teleportation wurde abgebrochen, da du dich bewegt hast.

# Commands
command.help.title=$prefix$ &7&nHilfe
command.help.help=&7 - &e/waypoints &7&l|&r&7 Diese Hilfe anzeigen
command.help.set.private=&7 - &e/waypoints set <Name> &7&l|&r&7 Setze einen neuen Wegpunkt
command.help.set.public=&7 - &e/waypoints setPublic <Name> &7&l|&r&7 Setze einen neuen öffentlichen Wegpunkt
command.help.set.permission=&7 - &e/waypoints setPermission <Berechtigung> <Name> &7&l|&r&7 Setze einen neuen eingeschränkten Wegpunkt
command.help.compass=&7 - &e/waypoints compass &7&l|&r&7 Setze das standard Kompass Ziel, wenn in der Config der Modus auf "ingame" steht.
command.help.other=&7 - &e/waypoints other <Name|UUID> &7&l|&r&7 Begutachte die Wegpunkte eines anderen Spielers.
command.help.updateItem=&7 - &e/waypoints updateItem <Waypoint|Folder> <ID> &7&l|&r&7 Dieser Befehl wird verwendet um das Icon eines Wegpunktes oder Ordners zu ändern.
command.help.rename.normal=&7 - &e/waypoints rename <PrivateWaypoint|PublicWaypoint|PermissionWaypoint|Folder> <ID> &7&l|&r&7 Dieser Befehl wird verwendet um Wegpunkte und Order umzubenennen
command.help.rename.waypointOnly=&7 - &e/waypoints rename <PrivateWaypoint|PublicWaypoint|PermissionWaypoint> <ID> &7&l|&r&7 Dieser Befehl wird verwendet um Wegpunkte umzubenennen
command.help.rename.folderOnly=&7 - &e/waypoints rename <Folder> <ID> &7&l|&r&7 Dieser Befehl wird verwendet um Order umzubenennen

command.compass.disabled=$prefix$ &cDiese Art das standard Kompass-Ziel zu setzten wurde in der Config deaktiviert
command.compass.locked=$prefix$ &cDieser Befehl wurde in der Config deaktiviert.
command.compass.setSuccess=$prefix$ &7Das neue standard Kompass-Ziel wurde gesetzt.

command.set.private.wrongUsage|
|$prefix$ &cUm einen Wegpunkt zu erstellen, muss ein Name angegeben werden. Beispiel:
|&a/waypoints set Mein Zuhause
command.set.private.nameDuplicate=$prefix$ &cWegpunkte mit doppeltem Namen wurden deaktiviert.
command.set.private.limitReached=$prefix$ &cDu hast das Limit an persönlichen Wegpunkten erreicht.
command.set.private.success=$prefix$ &7Der Wegpunkt wurde erfolgreich gesetzt.

command.set.public.wrongUsage|
|$prefix$ &cUm einen öffentlichen Wegpunkt zu erstellen, muss ein Name angegeben werden. Beispiel:
|&a/waypoints setPublic Spawn
command.set.public.nameDuplicate=$prefix$ &cÖffentliche Wegpunkte mit doppelten Namen wurden deaktiviert.
command.set.public.success=$prefix$ &7Der öffentliche Wegpunkt wurde erfolgreich gesetzt.

command.set.permission.wrongUsage|
|$prefix$ &cUm einen eingeschränkten Wegpunkt zu erstellen, muss eine Berechtigung und ein Name angegeben werden. Beispiel:
|&a/waypoints setPermission vip.secret Geheimversteck
command.set.permission.nameDuplicate=$prefix$ &cEingeschränkte Wegpunkte mit doppelten Namen wurden deaktiviert.
command.set.permission.success=$prefix$ &7Der eingeschränkte Wegpunkt wurde erfolgreich gesetzt.

command.createFolder.wrongUsage|
|$prefix$ &cUm einen Ordner zu erstellen musst du einen Namen angeben. Beispiel:
|&a/waypoints createFolder Nether Portale
command.createFolder.nameDuplicate=$prefix$ &cOrdner mit doppeltem Namen wurden deaktiviert.
command.createFolder.limitReached=$prefix$ &cDu hast das Limit an persönlichen Ordern erreicht.
command.createFolder.success=$prefix$ &7Order wurde erfolgreich erstellt.

command.other.wrongUsage|
|$prefix$ &cUm die Wegpunkte eines anderen Spielers einsehen zu können musst du deren Namen oder UUID angeben. Beispiel:
|&a/waypoints other Sytm
|&a/waypoints other 68f22021-732e-4afe-9cf5-6906f07a41db
command.other.notAValidUuidOrPlayerName=$prefix$ &cDie angegebene Identifizierung ist weder ein Name noch eine UUID.
command.other.uuidNotFound=$prefix$ &cEin Spieler mit dieser UUID konnte nicht gefunden werden.
command.other.playerNameNotFound=$prefix$ &cEin Spieler mit diesem Namen konnte nicht gefunden werden.

command.updateItem.disabled=$prefix$ &cDas aktualisieren von Icons wurde deaktiviert.
command.updateItem.notAValidItem=$prefix$ &cDas Item das du verwenden möchtest, ist nicht erlaubt.
command.updateItem.wrongUsage|
|$prefix$ &cUm das Icon zu aktualisieren musst du angeben ob es ein Ordner oder Wegpunkt ist und dessen ID. Beispiele:
|&a/waypoints updateItem waypoint 729dad02-6115-46a9-a2a3-ab6cb2c36c35
|&a/waypoints updateItem folder 59bcef04-9d5f-4a05-8fa4-f1f7b5311870
command.updateItem.waypoint.success=$prefix$ &7Das Icon vom Wegpunkt wurde erfolgreich aktualisiert.
command.updateItem.folder.success=$prefix$ &7Das Icon vom Ordner wurde erfolgreich aktualisiert.

command.rename.disabled=$prefix$ &cUmbenennen wurde deaktiviert.
command.rename.viaCmdDisabled=$prefix$ &cDas umbenennen von Wegpunkten mit diesem Befehl ist deaktiviert, bitte benutze die GUI.
command.rename.waypoint.private.disabled=$prefix$ &cDas Umbenennen von privaten Wegpunkten wurde deaktiviert.
command.rename.waypoint.public.disabled=$prefix$ &cDas Umbenennen von öffentlichen Wegpunkten wurde deaktiviert.
command.rename.waypoint.permission.disabled=$prefix$ &cDas Umbenennen von eingeschränkten Wegpunkten wurde deaktiviert.
command.rename.folder.disabled=$prefix$ &cDas Umbenennen von Ordnern wurde deaktiviert.
command.rename.wrongUsage|
|$prefix$ &cUm den Namen zu ändern musst du die Art des Wegpunktes/Ordner, dessen ID und den neuen Namen angeben. Beispiel:
|&a/waypoints rename privateWaypoint 729dad02-6115-46a9-a2a3-ab6cb2c36c35 Nether Portal No. 2
|&a/waypoints updateItem folder 59bcef04-9d5f-4a05-8fa4-f1f7b5311870 Mines
command.rename.waypoint.private.nameDuplicate=$prefix$ &cDoppelte Namen für private Wegpunkte wurden deaktiviert.
command.rename.waypoint.public.nameDuplicate=$prefix$ &cDoppelte Namen für öffentliche Wegpunkte wurden deaktiviert.
command.rename.waypoint.permission.nameDuplicate=$prefix$ &cDoppelte Namen für eingeschränkte Wegpunkte wurden deaktiviert.
command.rename.waypoint.success=$prefix$ &7Der Wegpunkt wurde erfolgreich umbenannt.
command.rename.folder.nameDuplicate=$prefix$ &cDoppelte Namen für Order wurden deaktiviert.
command.rename.folder.success=$prefix$ &7Der Ordner wurde erfolgreich unbenannt.

command.scripting.help.title=$prefix$ &7&nScripting-Hilfe
command.scripting.help.clearWaypoint=&7 - &e/waypointscript clearWaypoint <Name> &7&l|&r&7 Den aktiven Wegpunkt eines Spielers leeren
command.scripting.help.selectWaypoint=&7 - &e/waypointscript selectWaypoint <Name> <ID> &7&l|&r&7 Setze den aktiven Wegpunkt eines Spielers zu einem anderen

command.scripting.clearWaypoint.wrongUsage|
|$prefix$ &cDu musst den Namen von dem Spieler angeben, von welchen der Wegpunkt deaktiviert werden soll. Beispiel:
|&a/waypointscript clearWaypoint Sytm

command.scripting.selectWaypoint.wrongUsage|Du musst den Namen vom Spieler und die ID vom Wegpunkt angeben um
|$prefix$ &cYou need to provide the name of the player and the id of the waypoint to set the active waypoint
|&a/waypointscript selectWaypoint Sytm 729dad02-6115-46a9-a2a3-ab6cb2c36c35

command.notFound=$prefix$ &cThe command you entered was not found


# Inventory
variable.empty=&0-
variable.waypoints.select.displayName=&eWegpunkt auswählen
variable.waypoints.select.description=&7Drücke hier drauf, damit dieser Wegpunkt dir angezeigt wird.
variable.waypoints.delete.displayName=&cDen Wegpunkt löschen
variable.waypoints.delete.description=&0-
variable.waypoints.rename.displayName=&eDen Wegpunkt umbenennen
variable.waypoints.rename.description=&7Drücke hier drauf, um einen Nachricht zu erhalten, welche wenn man drauf drückt den Befehl zum umbenennen gibt.
variable.waypoints.teleport.displayName=&eZum Wegpunkt teleportieren
variable.waypoints.teleport.description=&0-
variable.waypoints.teleport.payment.xpPoints.description=&7Du benötigst &e%points%&7 Erfahrungspunkte um diese teleportation durchführen zu können
variable.waypoints.teleport.payment.xpLevels.description=&7Du benötigst &e%levels%&7 Erfahrungs-Level um diese teleportation durchführen zu können
variable.waypoints.teleport.payment.vault.description=&7Du benötigst &e%money%&7€ um diese teleportation durchführen zu können
variable.waypoints.selectBeaconColor.displayName=&7Ändere die Farbe des Leuchtfeuer-Strahls
variable.waypoints.selectBeaconColor.description=&eÄndere die Farbe des Leuchtfeuer-Strahls der zu sehen ist, wenn dieser Wegpunkt aktiv ist
variable.waypoints.getId.displayName=&7Erhalte die Wegpunkt ID
variable.waypoints.getId.description=&eSchickt eine Nachricht an dich mit der ID

inventory.title.own=&eWegpunkte
# %name% = Other players name
inventory.title.other=&eWegpunkte von &6%name%

inventory.general.previous.displayName=&eVorherige Seite
inventory.general.previous.description=$empty$
inventory.general.next.displayName=&eNächste Seite
inventory.general.next.description=$empty$
inventory.general.back.displayName=&eZurück
inventory.general.back.description=$empty$

inventory.cycleSort.displayName=&eSortierungs Modus
inventory.cycleSort.description=&eDrücke hier drauf um durch alle Sortierungs Modis zu wechseln.
# %name% = Name of the sort mode
inventory.cycleSort.active=&6%name%
# %name% = Name of the sort mode
inventory.cycleSort.inactive=&8%name%
inventory.cycleSort.mode.name.ascending=Name (Aufsteigend)
inventory.cycleSort.mode.name.descending=Name (Absteigend)
inventory.cycleSort.mode.createdAt.ascending=Erstellt (Aufsteigend)
inventory.cycleSort.mode.createdAt.descending=Erstellt (Absteigend)
inventory.cycleSort.mode.type=Art

inventory.overview.background.displayName=$empty$
inventory.overview.background.description=$empty$
inventory.overview.deselect.displayName=&eAktiven Wegpunkt abwählen
inventory.overview.deselect.description=&7Drücke hier drauf, wenn du den Wegpunkt erreicht hast, und die Richtungs Weiser nicht mehr benötigst.
inventory.overview.toggleGlobals.shown.displayName=&eGlobale Wegpunkte sind sichtbar
inventory.overview.toggleGlobals.shown.description=&7Drücke hier drauf um sie zu verstecken.
inventory.overview.toggleGlobals.hidden.displayName=&eGlobale Wegpunkte sind versteckt
inventory.overview.toggleGlobals.hidden.description=&7Drücke hier drauf um sie zu zeigen.
inventory.overview.setWaypoint.displayName=&eNeuen Wegpunkt setzten
inventory.overview.setWaypoint.description=$empty$
inventory.overview.createFolder.displayName=&eNeuen Ordner erstellen
inventory.overview.createFolder.description=$empty$

inventory.selectWaypointType.background.displayName=$empty$
inventory.selectWaypointType.background.description=$empty$
inventory.selectWaypointType.title.displayName=&eWelche Art von Wegpunkt möchtest du erstellen?
inventory.selectWaypointType.title.description=$empty$
inventory.selectWaypointType.private.displayName=&eEinen persönlichen Wegpunkt
inventory.selectWaypointType.private.description=$empty$
inventory.selectWaypointType.public.displayName=&eEinen öffentlichen Wegpunkt
inventory.selectWaypointType.public.description=$empty$
inventory.selectWaypointType.permission.displayName=&eEinen eingeschränkten Wegpunkt
inventory.selectWaypointType.permission.description=$empty$

inventory.waypoint.distance.otherWorld=?

inventory.waypoint.death.displayName=&cTodes-Wegpunkt
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.death.description|
|&7Welt: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distanz: &6%distance%
inventory.waypoint.death.background.displayName=$empty$
inventory.waypoint.death.background.description=$empty$
inventory.waypoint.death.select.displayName=$waypoints.select.displayName$
inventory.waypoint.death.select.description=$waypoints.select.description$
inventory.waypoint.death.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.death.teleport.description=$waypoints.teleport.description$
inventory.waypoint.death.teleport.payment.xpPoints.description=$variable.waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.death.teleport.payment.xpLevels.description=$variable.waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.death.teleport.payment.vault.description=$variable.waypoints.teleport.payment.vault.description$

# %name% = Name of the waypoint
inventory.waypoint.private.displayName=&5%name%
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.private.description|
|&7Welt: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distanz: &6%distance%
inventory.waypoint.private.background.displayName=$empty$
inventory.waypoint.private.background.description=$empty$
inventory.waypoint.private.select.displayName=$waypoints.select.displayName$
inventory.waypoint.private.select.description=$waypoints.select.description$
inventory.waypoint.private.delete.displayName=$waypoints.delete.displayName$
inventory.waypoint.private.delete.description=$waypoints.delete.description$
inventory.waypoint.private.rename.displayName=$waypoints.rename.displayName$
inventory.waypoint.private.rename.description=$waypoints.rename.description$
inventory.waypoint.private.moveToFolder.displayName=&eWegpunkt in einen Ordner verschieben
inventory.waypoint.private.moveToFolder.description=&7Gruppiere den Wegpunkt in einem Ordner oder entferne ihn von einem Ordner
inventory.waypoint.private.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.private.teleport.description=$waypoints.teleport.description$
inventory.waypoint.private.teleport.payment.xpPoints.description=$variable.waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.private.teleport.payment.xpLevels.description=$variable.waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.private.teleport.payment.vault.description=$variable.waypoints.teleport.payment.vault.description$
inventory.waypoint.private.selectBeaconColor.displayName=$waypoints.selectBeaconColor.displayName$
inventory.waypoint.private.selectBeaconColor.description=$waypoints.selectBeaconColor.description$

# %name% = Name of the waypoint
inventory.waypoint.public.displayName=&b%name%
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.public.description|
|&7Welt: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distanz: &6%distance%
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
inventory.waypoint.public.teleport.payment.xpPoints.description=$variable.waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.public.teleport.payment.xpLevels.description=$variable.waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.public.teleport.payment.vault.description=$variable.waypoints.teleport.payment.vault.description$
inventory.waypoint.public.selectBeaconColor.displayName=$waypoints.selectBeaconColor.displayName$
inventory.waypoint.public.selectBeaconColor.description=$waypoints.selectBeaconColor.description$
inventory.waypoint.public.getId.displayName=$waypoints.getId.displayName$
inventory.waypoint.public.getId.description=$waypoints.getId.description$

# %name% = Name of the waypoint
inventory.waypoint.permission.displayName=&d%name%
# %world% = Name of the world
# %x%, %y%, %z% = Coordinates of the waypoint as floating point number
# %blockX%, %blockY%, %blockZ% = The coordinates, but rounded to the block
# %distance% = The distance to the waypoint
inventory.waypoint.permission.description|
|&7Welt: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Distanz: &6%distance%
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
inventory.waypoint.permission.teleport.payment.xpPoints.description=$variable.waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.permission.teleport.payment.xpLevels.description=$variable.waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.permission.teleport.payment.vault.description=$variable.waypoints.teleport.payment.vault.description$
inventory.waypoint.permission.selectBeaconColor.displayName=$waypoints.selectBeaconColor.displayName$
inventory.waypoint.permission.selectBeaconColor.description=$waypoints.selectBeaconColor.description$
inventory.waypoint.permission.getId.displayName=$waypoints.getId.displayName$
inventory.waypoint.permission.getId.description=$waypoints.getId.description$

inventory.selectBeaconColor.background.displayName=$empty$
inventory.selectBeaconColor.background.description=$empty$
inventory.selectBeaconColor.previous.displayName=&7Nach links
inventory.selectBeaconColor.previous.description=$empty$
inventory.selectBeaconColor.next.displayName=&7Nach rechts
inventory.selectBeaconColor.next.description=$empty$
inventory.selectBeaconColor.clear.displayName=&fKlar
inventory.selectBeaconColor.clear.description=$empty$
inventory.selectBeaconColor.lightGray.displayName=&7Hellgrau
inventory.selectBeaconColor.lightGray.description=$empty$
inventory.selectBeaconColor.gray.displayName=&8Grau
inventory.selectBeaconColor.gray.description=$empty$
inventory.selectBeaconColor.pink.displayName=&dRosa
inventory.selectBeaconColor.pink.description=$empty$
inventory.selectBeaconColor.lime.displayName=&aHellgrün
inventory.selectBeaconColor.lime.description=$empty$
inventory.selectBeaconColor.yellow.displayName=&eGelb
inventory.selectBeaconColor.yellow.description=$empty$
inventory.selectBeaconColor.lightBlue.displayName=&9Hellblau
inventory.selectBeaconColor.lightBlue.description=$empty$
inventory.selectBeaconColor.magenta.displayName=&dMagenta
inventory.selectBeaconColor.magenta.description=$empty$
inventory.selectBeaconColor.orange.displayName=&6Orange
inventory.selectBeaconColor.orange.description=$empty$
inventory.selectBeaconColor.white.displayName=&fWeiß
inventory.selectBeaconColor.white.description=$empty$
inventory.selectBeaconColor.black.displayName=&0Schwarz
inventory.selectBeaconColor.black.description=$empty$
inventory.selectBeaconColor.red.displayName=&4Rot
inventory.selectBeaconColor.red.description=$empty$
inventory.selectBeaconColor.green.displayName=&2Grün
inventory.selectBeaconColor.green.description=$empty$
inventory.selectBeaconColor.brown.displayName=&6Braun
inventory.selectBeaconColor.brown.description=$empty$
inventory.selectBeaconColor.blue.displayName=&1Blau
inventory.selectBeaconColor.blue.description=$empty$
inventory.selectBeaconColor.cyan.displayName=&bTürkis
inventory.selectBeaconColor.cyan.description=$empty$
inventory.selectBeaconColor.purple.displayName=&5Violett
inventory.selectBeaconColor.purple.description=$empty$

inventory.selectFolder.background.displayName=$empty$
inventory.selectFolder.background.description=$empty$
inventory.selectFolder.noFolder.displayName=&eKeinen Ordner
inventory.selectFolder.noFolder.description=&7Entferne den Wegpunkt aus dem Ordner und füge ihm den Hauptbildschirm wieder hinzu.

# %name% = Folder name
inventory.folder.private.displayName=&b%name%
# %amount% = Amount of waypoints in folder
inventory.folder.private.description=&7Wegpunkte in diesem Ordner: &6%amount%
inventory.folder.private.background.displayName=$empty$
inventory.folder.private.background.description=$empty$
inventory.folder.private.delete.displayName=&4Ordner löschen
inventory.folder.private.delete.description=$empty$
inventory.folder.private.rename.displayName=&eOrdner umbenennen
inventory.folder.private.rename.description=&7Drücke hier drauf, um einen Nachricht zu erhalten, welche wenn man drauf drückt den Befehl zum umbenennen gibt.

inventory.folder.public.displayName=&bGlobale Wegpunkte
# %amount% = Amount of waypoints in folder
inventory.folder.public.description|
|&7Wegpunkte hier drin sind für jeden verfügbar.
|
|&7Im Moment sind &6%amount% &7Wegpunkte verfügbar
inventory.folder.public.background.displayName=$empty$
inventory.folder.public.background.description=$empty$

inventory.folder.permission.displayName=&dEingeschränkte Wegpunkte
# %amount% = Amount of waypoints in folder
inventory.folder.permission.description|
|&7Wegpunkte hier drin sind nur für einen verfügbar,
|&7wenn man die benötigte Berechtigung hat.
|
|&6%amount% &7Wegpunkte sind für dich verfügbar
inventory.folder.permission.background.displayName=$empty$
inventory.folder.permission.background.description=$empty$

inventory.confirmMenu.background.displayName=$empty$
inventory.confirmMenu.background.description=$empty$

inventory.confirmMenu.waypoint.private.delete.title.displayName=&7Bist du dir ganz sicher, dass du deinen Wegpunkt löschen möchtest?
inventory.confirmMenu.waypoint.private.delete.title.description=$empty$
inventory.confirmMenu.waypoint.private.delete.yes.displayName=&7&lJa&7, Wegpunkt löschen
inventory.confirmMenu.waypoint.private.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.private.delete.no.displayName=&7&lNein&7, Wegpunkt nicht löschen
inventory.confirmMenu.waypoint.private.delete.no.description=$empty$

inventory.confirmMenu.waypoint.public.delete.title.displayName=&7Bist du dir ganz sicher, dass du diesen öffentlichen Wegpunkt löschen möchtest?
inventory.confirmMenu.waypoint.public.delete.title.description=$empty$
inventory.confirmMenu.waypoint.public.delete.yes.displayName=&7&lJa&7, Wegpunkt löschen
inventory.confirmMenu.waypoint.public.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.public.delete.no.displayName=&7&lNein&7, Wegpunkt nicht löschen
inventory.confirmMenu.waypoint.public.delete.no.description=$empty$

inventory.confirmMenu.waypoint.permission.delete.title.displayName=&7Bist du dir ganz sicher, dass du diesen eingeschränkten Wegpunkt löschen möchtest?
inventory.confirmMenu.waypoint.permission.delete.title.description=$empty$
inventory.confirmMenu.waypoint.permission.delete.yes.displayName=&7&lJa&7, Wegpunkt löschen
inventory.confirmMenu.waypoint.permission.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.permission.delete.no.displayName=&7&lNein&7, Wegpunkt nicht löschen
inventory.confirmMenu.waypoint.permission.delete.no.description=$empty$

inventory.confirmMenu.folder.private.delete.title.displayName=&7Bist du dir ganz sicher, dass du deinen Ordner löschen möchtest?
inventory.confirmMenu.folder.private.delete.title.description=$empty$
inventory.confirmMenu.folder.private.delete.yes.displayName=&7&lJa&7, Ordner löschen
inventory.confirmMenu.folder.private.delete.yes.description=$empty$
inventory.confirmMenu.folder.private.delete.no.displayName=&7&lNein&7, Ordner nicht löschen
inventory.confirmMenu.folder.private.delete.no.description=$empty$

inventory.anvilGUI.enterNameHere=Gebe Name hier ein
inventory.anvilGUI.enterPermissionHere=Gebe Berechtigung hier ein