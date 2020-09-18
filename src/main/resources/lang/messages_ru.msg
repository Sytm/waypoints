variable.prefix=&b&lПутевые точки &e&l>&r

# General

general.notAPlayer=$prefix$ &cВы должны быть игроком, чтобы использовать эту команду.
general.noPermission=$prefix$ &cУ вас нет на это прав.
general.waypointNotFound=$prefix$ &cНе удалось найти путевую точку с этим идентификатором.
general.folderNotFound=$prefix$ &cНе удалось найти папку с таким идентификатором.
general.notAValidUuid=$prefix$ &cУказанный идентификатор имеет недопустимый формат.

# %currentworld% = Имя мира, в котором находится игрок
# %correctworld% = Название мира, где находится путевая точка
display.wrongWorld=&cВы сейчас находитесь в мире &a%currentworld%&c, а путевая точка находится в &a%correctworld%

chat.duration.second=секунда
chat.duration.seconds=секунд
chat.duration.minute=минута
chat.duration.minutes=минут
chat.duration.hour=час
chat.duration.hours=часов
chat.duration.day=день
chat.duration.days=дней

variable.chatAction.updateItem.waypoint=&7Нажмите здесь, когда вы держите предмет, который хотите использовать в качестве значка новой путевой точки..

chatAction.updateItem.waypoint.private=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.waypoint.public=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.waypoint.permission=$prefix$ $chatAction.updateItem.waypoint$
chatAction.updateItem.folder.private=$prefix$ &7Щелкните здесь, когда вы держите элемент, который хотите использовать в качестве значка новой папки.

chatAction.rename.waypoint.private=$prefix$ &7Нажмите здесь, чтобы получить команду переименовать путевую точку.
chatAction.rename.waypoint.public=$prefix$ &7Нажмите здесь, чтобы получить команду переименовать общественную путевую точку.
chatAction.rename.waypoint.permission=$prefix$ &7Щелкните здесь, чтобы получить команду для переименования разрешения путевой точки .
chatAction.rename.folder.private=$prefix$ &7Щелкните здесь, чтобы получить команду для переименования папки.

chatWarning.worldTranslationsMissing=$prefix$ &cНекоторые переводы мировых названий отсутствуют.

# %remainingTime% В некоторых переводах мировых названий отсутствует время, которое осталось до того, как игрок снова сможет телепортироваться.
chat.teleport.onCooldown=$prefix$ &cВы не можете телепортироваться прямо сейчас, потому что это время восстановления. Вы можете снова телепортироваться через&e%remainingTime%&c.
chat.teleport.notEnough.xpPoints=$prefix$ &cУ вас недостаточно очков опыта для выполнения этой телепортации.
chat.teleport.notEnough.xpLevels=$prefix$ &cУ вас недостаточно опыта для выполнения этой телепортации.
chat.teleport.notEnough.balance=$prefix$ &cУ вас недостаточно баланса для выполнения этой телепортации.
# %timeRequired% время, когда игрок должен стоять на месте
chat.teleport.standStillNotice=$prefix$ &7Вам нужно стоять на месте &e%timeRequired%&7 чтобы иметь возможность телепортироваться.
chat.teleport.cancelledMove=$prefix$ &cВаша телепортация была отменена, потому что вы подвинулись.

# Commands
command.help.title=$prefix$ &7&nПомощь
command.help.help=&7 - &e/waypoints &7&l|&r&7 Показать эту помощь
command.help.set.private=&7 - &e/waypoints set <Name> &7&l|&r&7 Создать новую путевую точку
command.help.set.public=&7 - &e/waypoints setPublic <Name> &7&l|&r&7 Создать новую общедоступную путевую точку
command.help.set.permission=&7 - &e/waypoints setPermission <Permission> <Name> &7&l|&r&7 Создать новое разрешение путевой точке
command.help.compass=&7 - &e/waypoints compass &7&l|&r&7 Устанавливает новую цель компаса по умолчанию, когда установлено значение "ingame" «внутри игры».
command.help.other=&7 - &e/waypoints other <Name|UUID> &7&l|&r&7 Просмотр путевых точек другого игрока
command.help.updateItem=&7 - &e/waypoints updateItem <Waypoint|Folder> <ID> &7&l|&r&7 Эта команда используется для обновления значков путевых точек и папок.
command.help.rename.normal=&7 - &e/waypoints rename <PrivateWaypoint|PublicWaypoint|PermissionWaypoint|Folder> <ID> &7&l|&r&7 Эта команда используется для переименования путевых точек или папок.
command.help.rename.waypointOnly=&7 - &e/waypoints rename <PrivateWaypoint|PublicWaypoint|PermissionWaypoint> <ID> &7&l|&r&7 Эта команда используется для переименования путевых точек.
command.help.rename.folderOnly=&7 - &e/waypoints rename <Folder> <ID> &7&l|&r&7 Эта команда используется для переименования папок

command.compass.disabled=$prefix$ &cЭтот метод установки цели компаса по умолчанию не был включен в конфигурации.
command.compass.locked=$prefix$ &cЭта команда отключена в конфигурации.
command.compass.setSuccess=$prefix$ &7Установлена новая цель компаса по умолчанию.

command.set.private.wrongUsage|
|$prefix$ &cВам нужно указать имя, чтобы создать новую путевую точку.К примеру:
|&a/waypoints set My Home
command.set.private.nameDuplicate=$prefix$ &cПутевые точки с повторяющимися именами отключены.
command.set.private.limitReached=$prefix$ &cВы достигли своего личного лимита путевых точек, которые вы можете установить.
command.set.private.success=$prefix$ &7Путевая точка была успешно установлена.

command.set.public.wrongUsage|
|$prefix$ &cВам необходимо указать имя, чтобы создать новую общедоступную путевую точку.К примеру:
|&a/waypoints setPublic Spawn
command.set.public.nameDuplicate=$prefix$ &cПубличные путевые точки с повторяющимися названиями отключены.
command.set.public.success=$prefix$ &7Общедоступная путевая точка установлена успешно.

command.set.permission.wrongUsage|
|$prefix$ &cВам необходимо предоставить разрешение и имя для создания нового разрешения путевой точки . К примеру:
|&a/waypoints setPermission ranks.vip.secretHideout Секретное убежище
command.set.permission.nameDuplicate=$prefix$ &cПутевые точки разрешений с повторяющимися именами отключены.
command.set.permission.success=$prefix$ &7Путевая точка с разрешением установлена успешно.

command.createFolder.wrongUsage|
|$prefix$ &cВам необходимо указать имя для создания новой папки. Пример:
|&a/waypoints createFolder Nether portals
command.createFolder.nameDuplicate=$prefix$ &cПапки с повторяющимися именами отключены.
command.createFolder.limitReached=$prefix$ &cВы достигли своего личного лимита папок, которые можете создавать.
command.createFolder.success=$prefix$ &7Папка успешно создана.

command.other.wrongUsage|
|$prefix$ &cЧтобы просмотреть путевые точки другого игрока, вам необходимо указать его имя или uuid. Пример:
|&a/waypoints other Sytm
|&a/waypoints other 68f22021-732e-4afe-9cf5-6906f07a41db
command.other.notAValidUuidOrPlayerName=$prefix$ &cПредоставленный идентификатор не является ни именем пользователя Minecraft, ни действующим uuid.
command.other.uuidNotFound=$prefix$ &cНе удалось найти игрока с таким uuid.
command.other.playerNameNotFound=$prefix$ &cНе удалось найти игрока с таким именем.

command.updateItem.disabled=$prefix$ &cОбновление элементов отключено.
command.updateItem.notAValidItem=$prefix$ &cПредмет, который вы пытаетесь использовать, запрещен.
command.updateItem.wrongUsage|
|$prefix$ &cЧтобы обновить элемент, вам необходимо указать, является ли он папкой или путевой точкой, и указать его идентификатор. Пример:
|&a/waypoints updateItem waypoint 729dad02-6115-46a9-a2a3-ab6cb2c36c35
|&a/waypoints updateItem folder 59bcef04-9d5f-4a05-8fa4-f1f7b5311870
command.updateItem.waypoint.success=$prefix$ &7Элемент путевой точки успешно обновлен.
command.updateItem.folder.success=$prefix$ &7Элемент папки успешно обновлен.

command.rename.disabled=$prefix$ &cПереименование отключено.
command.rename.viaCmdDisabled=$prefix$ &cПереименование путевых точек с помощью команды отключено, используйте графический интерфейс.
command.rename.waypoint.private.disabled=$prefix$ &cПереименование частных путевых точек отключено.
command.rename.waypoint.public.disabled=$prefix$ &cПереименование общедоступных путевых точек отключено.
command.rename.waypoint.permission.disabled=$prefix$ &cПереименование путевых точек отключено.
command.rename.folder.disabled=$prefix$ &cПереименование папок отключено.
command.rename.wrongUsage|
|$prefix$ &cВам необходимо указать новое имя и тип того, что вы хотите переименовать. Пример:
|&a/waypoints rename privateWaypoint 729dad02-6115-46a9-a2a3-ab6cb2c36c35 Nether Portal No. 2
|&a/waypoints updateItem folder 59bcef04-9d5f-4a05-8fa4-f1f7b5311870 Mines
command.rename.waypoint.private.nameDuplicate=$prefix$ &cДублирующие имена для путевых точек были отключены.
command.rename.waypoint.public.nameDuplicate=$prefix$ &cДублирующиеся имена для общедоступных путевых точек были отключены.
command.rename.waypoint.permission.nameDuplicate=$prefix$ &cДублирующиеся имена для путевых точек разрешений были отключены.
command.rename.waypoint.success=$prefix$ &7Путевая точка была успешно переименована.
command.rename.folder.nameDuplicate=$prefix$ &cДублирование имен папок отключено.
command.rename.folder.success=$prefix$ &7Папка была успешно переименована.

command.notFound=$prefix$ &cПапка успешно переименована. Введенная вами команда не найдена.


# Inventory
variable.empty=&0-
variable.waypoints.select.displayName=&eВыбрать путевую точку
variable.waypoints.select.description=&7Щелкните здесь, чтобы сделать эту путевую точку своей активной
variable.waypoints.delete.displayName=&cУдалить путевую точку
variable.waypoints.delete.description=&0-
variable.waypoints.rename.displayName=&eПереименовать путевую точку
variable.waypoints.rename.description=&7Щелкните здесь, чтобы получить сообщение, которое вы можете щелкнуть, чтобы получить команду переименования
variable.waypoints.teleport.displayName=&eТелепорт к путевой точке
variable.waypoints.teleport.description=&0-
variable.waypoints.teleport.payment.xpPoints.description=&7Вам нужно &e%points%&7 очков опыта для выполнения этой телепортации.
variable.waypoints.teleport.payment.xpLevels.description=&7Вам нужно &e%levels%&7 уровней опыта для выполнения этой телепортации
variable.waypoints.teleport.payment.vault.description=&7Вам нужно &e%money%&7€ для выполнения этой телепортации
variable.waypoints.selectBeaconColor.displayName=&7Измените цвет луча маяка
variable.waypoints.selectBeaconColor.description=&eИзмените цвет луча маяка, если эта путевая точка была выбрана

inventory.title.own=&eПутевые точки
# %name% = Имя других игроков
inventory.title.other=&6%name%'s &путевая точка

inventory.general.previous.displayName=&eПредыдущая страница
inventory.general.previous.description=$empty$
inventory.general.next.displayName=&eСледующая страница
inventory.general.next.description=$empty$
inventory.general.back.displayName=&eНазад
inventory.general.back.description=$empty$

inventory.cycleSort.displayName=&eРежим сортировки
inventory.cycleSort.description=&eЩелкните здесь, чтобы просмотреть различные режимы сортировки
# %name% = Название режима сортировки
inventory.cycleSort.active=&6%name%
# %name% = Название режима сортировки
inventory.cycleSort.inactive=&8%name%
inventory.cycleSort.mode.name.ascending=Имя (по возрастанию)
inventory.cycleSort.mode.name.descending=Имя (по убыванию)
inventory.cycleSort.mode.createdAt.ascending=Создано (по возрастанию)
inventory.cycleSort.mode.createdAt.descending=Создано (по убыванию)
inventory.cycleSort.mode.type=Тип

inventory.overview.background.displayName=$empty$
inventory.overview.background.description=$empty$
inventory.overview.deselect.displayName=&eОтменить выбор активной путевой точки
inventory.overview.deselect.description=&7Нажмите здесь, когда вы достигнете путевой точки и вам больше не нужны указатели поворота
inventory.overview.toggleGlobals.shown.displayName=&eГлобальные путевые точки видны
inventory.overview.toggleGlobals.shown.description=&7Щелкните здесь, чтобы скрыть их
inventory.overview.toggleGlobals.hidden.displayName=&eГлобальные путевые точки скрыты
inventory.overview.toggleGlobals.hidden.description=&7Нажмите здесь, чтобы показать им
inventory.overview.setWaypoint.displayName=&eУстановить новые путевые точки
inventory.overview.setWaypoint.description=$empty$
inventory.overview.createFolder.displayName=&eСоздать новую папку
inventory.overview.createFolder.description=$empty$

inventory.selectWaypointType.background.displayName=$empty$
inventory.selectWaypointType.background.description=$empty$
inventory.selectWaypointType.title.displayName=Какую путевую точку вы хотите установить?
inventory.selectWaypointType.title.description=$empty$
inventory.selectWaypointType.private.displayName=&eЧастная путевая точка
inventory.selectWaypointType.private.description=$empty$
inventory.selectWaypointType.public.displayName=&eОбщественная путевая точка
inventory.selectWaypointType.public.description=$empty$
inventory.selectWaypointType.permission.displayName=&eРазрешение Путевой точки 
inventory.selectWaypointType.permission.description=$empty$

inventory.waypoint.distance.otherWorld=?

inventory.waypoint.death.displayName=&cDeath-Waypoint
# %world% = Имя мира
# %x%, %y%, %z% = Координаты путевой точки в виде числа с плавающей запятой
# %blockX%, %blockY%, %blockZ% = Координаты, но округленные до блока
# %distance% = Расстояние до путевой точки
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

# %name% = Название путевой точки
inventory.waypoint.private.displayName=&5%name%
# %world% = Имя мира
# %x%, %y%, %z% = Координаты путевой точки в виде числа с плавающей запятой
# %blockX%, %blockY%, %blockZ% = Координаты, но округленные до блока
# %distance% = Расстояние до путевой точки
inventory.waypoint.private.description|
|&7World: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Расстояние: &6%distance%
inventory.waypoint.private.background.displayName=$empty$
inventory.waypoint.private.background.description=$empty$
inventory.waypoint.private.select.displayName=$waypoints.select.displayName$
inventory.waypoint.private.select.description=$waypoints.select.description$
inventory.waypoint.private.delete.displayName=$waypoints.delete.displayName$
inventory.waypoint.private.delete.description=$waypoints.delete.description$
inventory.waypoint.private.rename.displayName=$waypoints.rename.displayName$
inventory.waypoint.private.rename.description=$waypoints.rename.description$
inventory.waypoint.private.moveToFolder.displayName=&eПереместить путевую точку в папку
inventory.waypoint.private.moveToFolder.description=&7Поместите путевую точку в папку, чтобы сгруппировать ее, или удалите ее из текущей папки
inventory.waypoint.private.teleport.displayName=$waypoints.teleport.displayName$
inventory.waypoint.private.teleport.description=$waypoints.teleport.description$
inventory.waypoint.private.teleport.payment.xpPoints.description=$waypoints.teleport.payment.xpPoints.description$
inventory.waypoint.private.teleport.payment.xpLevels.description=$waypoints.teleport.payment.xpLevels.description$
inventory.waypoint.private.teleport.payment.vault.description=$waypoints.teleport.payment.vault.description$
inventory.waypoint.private.selectBeaconColor.displayName=$waypoints.selectBeaconColor.displayName$
inventory.waypoint.private.selectBeaconColor.description=$waypoints.selectBeaconColor.description$

# %name% = Название путевой точки
inventory.waypoint.public.displayName=&b%name%
# %world% = Имя мира
# %x%, %y%, %z% = Координаты путевой точки в виде числа с плавающей запятой
# %blockX%, %blockY%, %blockZ% = Координаты, но округленные до блока
# %distance% = Расстояние до путевой точки
inventory.waypoint.public.description|
|&7World: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Расстояние: &6%distance%
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
inventory.waypoint.public.selectBeaconColor.displayName=$waypoints.selectBeaconColor.displayName$
inventory.waypoint.public.selectBeaconColor.description=$waypoints.selectBeaconColor.description$

# %name% = Название путевой точки
inventory.waypoint.permission.displayName=&d%name%
# %world% = Имя мира
# %x%, %y%, %z% = Координаты путевой точки в виде числа с плавающей запятой
# %blockX%, %blockY%, %blockZ% = Координаты, но округленные до блока
# %distance% = Расстояние до путевой точки
inventory.waypoint.permission.description|
|&7Мир: &6%world%
|&7X: &6%blockX%
|&7Y: &6%blockY%
|&7Z: &6%blockZ%
|&7Расстояние: &6%distance%
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
inventory.waypoint.permission.selectBeaconColor.displayName=$waypoints.selectBeaconColor.displayName$
inventory.waypoint.permission.selectBeaconColor.description=$waypoints.selectBeaconColor.description$

inventory.selectBeaconColor.background.displayName=$empty$
inventory.selectBeaconColor.background.description=$empty$
inventory.selectBeaconColor.previous.displayName=&7Двигаться влево
inventory.selectBeaconColor.previous.description=$empty$
inventory.selectBeaconColor.next.displayName=&7Двигаться вправо
inventory.selectBeaconColor.next.description=$empty$
inventory.selectBeaconColor.clear.displayName=&fОчистить
inventory.selectBeaconColor.clear.description=$empty$
inventory.selectBeaconColor.lightGray.displayName=&7Светло-серый
inventory.selectBeaconColor.lightGray.description=$empty$
inventory.selectBeaconColor.gray.displayName=&8Серый
inventory.selectBeaconColor.gray.description=$empty$
inventory.selectBeaconColor.pink.displayName=&dРозовый
inventory.selectBeaconColor.pink.description=$empty$
inventory.selectBeaconColor.lime.displayName=&aЛайм
inventory.selectBeaconColor.lime.description=$empty$
inventory.selectBeaconColor.yellow.displayName=&eЖелтый
inventory.selectBeaconColor.yellow.description=$empty$
inventory.selectBeaconColor.lightBlue.displayName=&9Светло-синий
inventory.selectBeaconColor.lightBlue.description=$empty$
inventory.selectBeaconColor.magenta.displayName=&dФуксин
inventory.selectBeaconColor.magenta.description=$empty$
inventory.selectBeaconColor.orange.displayName=&6Оранжевый
inventory.selectBeaconColor.orange.description=$empty$
inventory.selectBeaconColor.white.displayName=&fБелый
inventory.selectBeaconColor.white.description=$empty$
inventory.selectBeaconColor.black.displayName=&0Черный
inventory.selectBeaconColor.black.description=$empty$
inventory.selectBeaconColor.red.displayName=&4Красный
inventory.selectBeaconColor.red.description=$empty$
inventory.selectBeaconColor.green.displayName=&2Зеленый
inventory.selectBeaconColor.green.description=$empty$
inventory.selectBeaconColor.brown.displayName=&6Коричневый
inventory.selectBeaconColor.brown.description=$empty$
inventory.selectBeaconColor.blue.displayName=&1Синий
inventory.selectBeaconColor.blue.description=$empty$
inventory.selectBeaconColor.cyan.displayName=&bБирюзовый
inventory.selectBeaconColor.cyan.description=$empty$
inventory.selectBeaconColor.purple.displayName=&5Фиолетовый
inventory.selectBeaconColor.purple.description=$empty$

inventory.selectFolder.background.displayName=$empty$
inventory.selectFolder.background.description=$empty$
inventory.selectFolder.noFolder.displayName=&eНет папки
inventory.selectFolder.noFolder.description=&7Вернуть эту путевую точку к основному обзору

# %name% = Имя папки
inventory.folder.private.displayName=&b%name%
# %amount% = Количество путевых точек в папке
inventory.folder.private.description=&7Путевые точки в этой папке: &6%amount%
inventory.folder.private.background.displayName=$empty$
inventory.folder.private.background.description=$empty$
inventory.folder.private.delete.displayName=&4Удалить папку
inventory.folder.private.delete.description=$empty$
inventory.folder.private.rename.displayName=&eПереименовать папку
inventory.folder.private.rename.description=&7Щелкните здесь, чтобы получить сообщение, которое вы можете щелкнуть, чтобы получить команду переименования

inventory.folder.public.displayName=&bГлобальные путевые точки
# %amount% = Количество путевых точек в папке
inventory.folder.public.description|
|&7Путевые точки здесь доступны каждому
|
|&7Доступны &6%amount% &7путевые точки
inventory.folder.public.background.displayName=$empty$
inventory.folder.public.background.description=$empty$

inventory.folder.permission.displayName=&dРазрешения Путевых точек
# %amount% = Количество путевых точек в папке
inventory.folder.permission.description|
|&7Путевые точки здесь доступны только вам,
|&7если у вас есть определенные разрешения
|
|&6%amount% &7путевые точки доступны для вас
inventory.folder.permission.background.displayName=$empty$
inventory.folder.permission.background.description=$empty$

inventory.confirmMenu.background.displayName=$empty$
inventory.confirmMenu.background.description=$empty$

inventory.confirmMenu.waypoint.private.delete.title.displayName=&7Вы уверены, что хотите удалить свою путевую точку?
inventory.confirmMenu.waypoint.private.delete.title.description=$empty$
inventory.confirmMenu.waypoint.private.delete.yes.displayName=&7&lДа&7, удалить
inventory.confirmMenu.waypoint.private.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.private.delete.no.displayName=&7&lНет&7, не удалять
inventory.confirmMenu.waypoint.private.delete.no.description=$empty$

inventory.confirmMenu.waypoint.public.delete.title.displayName=&7Вы уверены, что хотите удалить эту общедоступную путевую точку?
inventory.confirmMenu.waypoint.public.delete.title.description=$empty$
inventory.confirmMenu.waypoint.public.delete.yes.displayName=&7&lДа&7, удалить
inventory.confirmMenu.waypoint.public.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.public.delete.no.displayName=&7&lНет&7, не удалять
inventory.confirmMenu.waypoint.public.delete.no.description=$empty$

inventory.confirmMenu.waypoint.permission.delete.title.displayName=&7Вы действительно хотите удалить разрешение у путевой точки ?
inventory.confirmMenu.waypoint.permission.delete.title.description=$empty$
inventory.confirmMenu.waypoint.permission.delete.yes.displayName=&7&lДа&7, удалить
inventory.confirmMenu.waypoint.permission.delete.yes.description=$empty$
inventory.confirmMenu.waypoint.permission.delete.no.displayName=&7&lНет&7, не удалять
inventory.confirmMenu.waypoint.permission.delete.no.description=$empty$

inventory.confirmMenu.folder.private.delete.title.displayName=&7Вы уверены, что хотите удалить свою папку?
inventory.confirmMenu.folder.private.delete.title.description=$empty$
inventory.confirmMenu.folder.private.delete.yes.displayName=&7&lДа&7, удалить
inventory.confirmMenu.folder.private.delete.yes.description=$empty$
inventory.confirmMenu.folder.private.delete.no.displayName=&7&lНет&7, не удалять
inventory.confirmMenu.folder.private.delete.no.description=$empty$

inventory.anvilGUI.enterNameHere=Введите имя здесь
inventory.anvilGUI.enterPermissionHere=Введите разрешение здесь