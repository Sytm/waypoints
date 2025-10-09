package de.md5lukas.waypoints.config

import de.md5lukas.configurate.NonEmptyString
import de.md5lukas.configurate.Positive
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.pointers.BeaconColor
import de.md5lukas.waypoints.pointers.config.PointerConfiguration
import de.md5lukas.waypoints.util.Expression
import de.md5lukas.waypoints.util.MathParser
import java.time.Duration
import java.time.Period
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.inventory.ItemType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.PostProcess
import org.spongepowered.configurate.serialize.SerializationException

@ConfigSerializable
class WaypointsConfiguration {

  var database = Database()
    private set

  @ConfigSerializable
  class Database {
    @Comment(
        "Time period after which death waypoints are deleted. Set all values to zero to disable")
    var deathWaypointRetentionPeriod: Period = Period.ofDays(7)
      private set
  }

  var general = General()
    private set

  @ConfigSerializable
  class General {
    @Comment("Set the language for the plugin here")
    @NonEmptyString
    var language = "en"
      private set

    var updateChecker = true
      private set

    @Comment(
        """
      What to do when a waypoint is loaded that is in a world that has been deleted / renamed. Available options:
      SHOW: Shows the waypoint normally, but cannot be selected or teleported to
      HIDE: The waypoint will be hidden in the GUI, but will reappear once the world is back
      DELETE: The waypoint will be permanently deleted
    """)
    var worldNotFound = WorldNotFoundAction.HIDE
      private set

    @Comment(
        "When this option is enabled players will only see waypoints that are in the same world as themselves")
    var hideWaypointsFromDifferentWorlds = false
      private set
  }

  var features = Features()
    private set

  @ConfigSerializable
  class Features {
    @Comment("Set to \"false\" to disable global waypoints")
    var globalWaypoints = true
      private set

    @Comment("Set to \"false\" to disable death waypoints")
    var deathWaypoints = true
      private set

    @Comment("Set to \"false\" to disable teleportations for everyone entirely")
    var teleportation = true
      private set

    var publicOwnership = PublicOwnership()
      private set

    @ConfigSerializable
    class PublicOwnership {
      @Comment(
          "Set to \"true\" to allow all players to create public waypoints, but only be allowed to edit the ones they created")
      var waypoints = false
        private set

      @Comment(
          """
        Set to "true" to allow all players to create public folders, but only be allowed to edit the ones they created
        Players can only move waypoints into folders they created
      """)
      var folders = true
        get() = waypoints && field
        private set
    }
  }

  @Comment(
      "Specify additional aliases for the two commands Waypoints uses in case other plugins overwrite them")
  var commandAliases = CommandAliases()
    private set

  @ConfigSerializable
  class CommandAliases {
    var waypoints = setOf("wp")
      private set

    var waypointsScript = setOf("wps")
      private set
  }

  var pointToDeathWaypointOnDeath = PointToDeathWaypointOnDeath()
    private set

  @ConfigSerializable
  class PointToDeathWaypointOnDeath {
    var enabled = true
      private set

    @Comment(
        "When set to a value greater than zero, the death waypoint will be automatically deselected after the set time")
    var autoDeselectAfter: Duration = Duration.ZERO
      private set
  }

  @Comment(
      """
      Specify in which worlds waypoints can be created
      Players with the permission waypoints.modify.anywhere can place waypoints wherever they want.
      Automatic waypoint-creation in disabled worlds will not occur
    """)
  var availableWorlds = AvailableWorlds()
    private set

  @ConfigSerializable
  class AvailableWorlds {
    @Comment(
        """
        Available options:
        blacklist: Worlds in the list are not available
        whitelist: Only worlds on the list are available
      """)
    var type = FilterType.BLACKLIST
      private set

    // TODO set or list?
    var worlds = setOf("hub")
      private set

    @PostProcess
    fun postProcess() {
      worlds = worlds.mapTo(LinkedHashSet()) { it.lowercase() }
    }
  }

  var openWithItem = OpenWithItem()
    private set

  @ConfigSerializable
  class OpenWithItem {
    var enabled = true
      private set

    @Comment("Available options are RIGHT (right-click) and LEFT (left-click)")
    var click = ClickType.RIGHT
      private set

    @Comment(
        """
        If set to true the player must sneak and then use the item to open the inventory
        Otherwise it doesn't matter if the player is sneaking
      """)
    var mustSneak = true
      private set

    // TODO type
    @Comment("Any of the following item can be used to open the GUI")
    var items = setOf<ItemType>(ItemType.COMPASS)
      private set
  }

  @Comment(
      "When placing named banners, they will automatically create a private waypoint at the location of the banner")
  var bannerWaypoints = BannerWaypoints()
    private set

  @ConfigSerializable
  class BannerWaypoints {
    var enabled = true
      private set

    var bannerBreaking = BannerBreaking()
      private set

    @ConfigSerializable
    class BannerBreaking {
      @Comment("When breaking the banner, whether the waypoint should be removed as well")
      var removeWaypoint = true
        private set

      @Comment(
          "When true, the waypoint is only removed if the player who originally placed the banner was the one breaking it")
      var triggerOnlyForOwner = true
        private set
    }
  }

  var customIconFilter = CustomIconFilter()
    private set

  @ConfigSerializable
  class CustomIconFilter {
    @Comment(
        """
        Available options:
        blacklist: Items in the list are forbidden
        whitelist: Only items on the list are allowed
      """)
    var type = FilterType.BLACKLIST
      private set

    @Comment("air is always disallowed")
    var materials = setOf<ItemType>(ItemType.BARRIER, ItemType.BEDROCK)
      private set
  }

  var limits = Limits()
    private set

  @ConfigSerializable
  class Limits {

    var waypoints = Limits0(true)
      private set

    var folders = Limits0(false)
      private set

    @ConfigSerializable
    class Limits0(waypoints: Boolean) {
      constructor() : this(true)

      @Comment(
          "Maximum amount of private waypoints/folders a player can have. Players with the permission waypoints.unlimited are not affected by this restriction")
      @Positive(true)
      var limit = if (waypoints) 200 else 20
        private set

      @Comment(
          """
        Limit values to check for in permissions.
        For waypoints the checked permission looks like "waypoints.limit.waypoints.NUMBER" and for folders "waypoints.limit.folders.NUMBER"
        If the player has this permission, his limit is lifted to NUMBER. Higher numbers are checked first       
      """)
      var permissionLimits = setOf(if (waypoints) 400 else 40)
        private set

      @Comment(
          "These settings are only applicable if the feature \"publicOwnership.waypoints\" is enabled")
      var public = Public(waypoints)
        private set

      @ConfigSerializable
      class Public(waypoints: Boolean) {
        constructor() : this(true)

        @Comment(
            "Maximum amount of public waypoints/folders a player without the permission waypoints.modify.public can create")
        var limit = if (waypoints) 20 else 2
          private set

        @Comment(
            """
          Limit values to check for in permissions.
          For waypoints the checked permission looks like "waypoints.limit.waypoints.public.NUMBER" and for folders "waypoints.limit.folders.public.NUMBER"
          If the player has this permission, his limit is lifted to NUMBER. Higher numbers are checked first       
        """)
        var permissionLimits = setOf(if (waypoints) 40 else 4)
          private set

        @PostProcess
        fun postProcess() {
          permissionLimits = permissionLimits.toSortedSet(Comparator.reverseOrder())
        }
      }

      @Comment(
          "Allow or disallow duplicate names for private, public or permission waypoints/folders")
      var allowDuplicateNames = AllowDuplicateNames()
        private set

      @ConfigSerializable
      class AllowDuplicateNames {
        var private = true
          private set

        var public = false
          private set

        var permission = false
          private set
      }

      @PostProcess
      fun postProcess() {
        permissionLimits = permissionLimits.toSortedSet(Comparator.reverseOrder())
      }
    }
  }

  var teleport = Teleport()
    private set

  @ConfigSerializable
  class Teleport {
    @Comment(
        """
      When a player clicks the teleport button he has to stand still for at least x amount of time before getting teleported.
      Set to 0s to disable
    """)
    var standStillTime: Duration = Duration.ofSeconds(3)
      private set

    @Comment(
        """
      The radius in blocks a player needs to be in from a waypoint to mark that waypoint as visited for the player
      This should be bigger or the same size as the disableWhenReached radius
    """)
    var visitedRadius = 10L
      get() = field * field
      private set

    @Comment(
        """
      cooldown:
      The cooldown between each teleportation for a player
      Set to 0s to disable
      
      alsoApplyCooldownTo:
      When teleporting to a waypoint of this type, all listed types will also receive the same cooldown as this one
      
      mustVisit:
      If set to true, the player must have visited the waypoint before.
      To mark a waypoint as visited the player must have either created it at his current location without coordinates
      or have the waypoint selected and reach the visited radius
      Only applicable to non-death waypoints
      
      onlyLastWaypoint:
      Allows the player to only teleport to the last location they died at, not all of them.
      Only applicable to death waypoints
      
      paymentType:
      Available types are: disabled, free, xp (levels), xp_points, vault (your economy plugins currency)
      When using the payment method xp the returned value is rounded to the closest full value
      
      perCategory:
      Optionally the counter can be applied to the entire category (e.g. private, death, public, permission) per player or per waypoint per player
      This only affects the price of the teleportation
      
      maxCost:
      The maximum cost at which the result of the formula is capped at
      
      formula:
      You can provide a formula to calculate the price
      The following variables are available:
      - n => how often a player teleported
      - distance => the distance between the player and waypoint
      
      differentWorld.allow:
      Set to false to disallow teleportations to other worlds
      
      differentWorld.distance:
      The distance to assume between the player and waypoint if they are in different worlds, because now they cannot be properly measured anymore
    """)
    var private =
        TypedTeleport(
            cooldown = Duration.ofHours(24),
            alsoApplyCooldownTo = setOf(Type.DEATH),
            mustVisit = true,
            onlyLastWaypoint = false,
            perCategory = true,
            maxCost = 10,
            formula = "1 + n")
    var death =
        TypedTeleport(
            cooldown = Duration.ofHours(24),
            alsoApplyCooldownTo = setOf(Type.PRIVATE),
            mustVisit = false,
            onlyLastWaypoint = true,
            perCategory = true,
            maxCost = 10,
            formula = "10")
    var public =
        TypedTeleport(
            cooldown = Duration.ofHours(24),
            alsoApplyCooldownTo = emptySet(),
            mustVisit = true,
            onlyLastWaypoint = false,
            perCategory = false,
            maxCost = 8,
            formula = "2 + n")
    var permission =
        TypedTeleport(
            cooldown = Duration.ofHours(4),
            alsoApplyCooldownTo = emptySet(),
            mustVisit = false,
            onlyLastWaypoint = false,
            perCategory = false,
            maxCost = 3,
            formula = "n")

    @ConfigSerializable
    class TypedTeleport(
        cooldown: Duration,
        alsoApplyCooldownTo: Set<Type>,
        mustVisit: Boolean,
        onlyLastWaypoint: Boolean,
        perCategory: Boolean,
        maxCost: Long,
        formula: String
    ) {
      constructor() : this(Duration.ZERO, emptySet(), false, false, false, 0, "")

      var cooldown = cooldown
        private set

      var alsoApplyCooldownTo = alsoApplyCooldownTo
        private set

      var mustVisit = mustVisit
        private set

      var onlyLastWaypoint = onlyLastWaypoint
        private set

      var paymentType: TeleportPaymentType = TeleportPaymentType.DISABLED
        private set

      var perCategory = perCategory
        private set

      var maxCost = maxCost
        private set

      var formula = formula
        private set

      @Transient
      var parsedFormula: Expression = Expression { 0.0 }
        private set

      var differentWorld = DifferentWorld()
        private set

      @ConfigSerializable
      class DifferentWorld {

        var allow = true
          private set

        var distance = 1000.0
          private set
      }

      @PostProcess
      fun postProcess() {
        try {
          parsedFormula = MathParser.parse(formula, "n", "distance")
        } catch (e: Exception) {
          throw SerializationException(String::class.java, "Could not parse formula: $formula", e)
        }
      }
    }
  }

  var integrations = Integrations()
    private set

  @ConfigSerializable
  class Integrations {

    var geyser = Geyser()
      private set

    @ConfigSerializable
    class Geyser {
      var enabled = false
        private set

      @Comment("The icons to use for the tracking request menu")
      var icon = Icon()
        private set

      @ConfigSerializable
      class Icon {
        @NonEmptyString
        var accept = "textures/ui/confirm"
          private set

        @NonEmptyString
        var decline = "textures/ui/redX1"
          private set
      }
    }

    var dynmap = DynMap()
      private set

    @ConfigSerializable
    class DynMap {
      var enabled = true
        private set

      @Comment(
          "See https://github.com/webbukkit/dynmap/wiki/Using-Markers#marker-icons for more information")
      @NonEmptyString
      var icon = "default"
        private set
    }

    var squaremap = SquareMap()
      private set

    @ConfigSerializable
    class SquareMap {
      var enabled = true
        private set

      @Comment(
          """
        The icon id must either be the full key of an existing icon that got registered by another plugin (see "plugins/squaremap/web/images/icon/registered")
        Or it must be the name of an icon in the folder "plugins/Waypoints/icons".
        Examples:
        plugins/squaremap/web/images/icon/registered/squaremap-spawn_icon.png -> squaremap-spawn_icon.png
        plugins/Waypoints/icons/special.png -> special
      """)
      @NonEmptyString
      var icon = "w"
        private set

      @Positive
      var iconSize: Int = 20
        private set
    }

    var pl3xmap = Pl3xMap()
      private set

    @ConfigSerializable
    class Pl3xMap {
      var enabled = true
        private set

      @Comment(
          """
        The icon id must either be the full key of an existing icon that got registered by another plugin (see "plugins/Pl3xMap/web/images/icon/registered")
        Or it must be the name of an icon in the folder "plugins/Waypoints/icons".
        Examples:
        plugins/Pl3xMap/web/images/icon/registered/spawn.png -> spawn
        plugins/Waypoints/icons/special.png -> special
      """)
      @NonEmptyString
      var icon = "w"
        private set

      @Positive
      var iconSize: Double = 20.0
        private set
    }

    var bluemap = BlueMap()
      private set

    @ConfigSerializable
    class BlueMap {
      var enabled = true
        private set
    }
  }

  var playerTracking = PlayerTracking()
    private set

  @ConfigSerializable
  class PlayerTracking {
    var enabled = false
      private set

    @Comment("When true, players can enable / disable being able to be tracked in the GUI")
    var toggleable = true
      private set

    @Comment(
        "When true, players can only track other players when they themselves can be tracked by other players")
    var trackingRequiresTrackable = false
      private set

    var request = Request()
      private set

    @ConfigSerializable
    class Request {
      @Comment(
          "When true, the player to be tracked first needs to accept the request of the tracking player to begin tracking")
      var enabled = false
        private set

      @Comment("The amount of time the request is valid for")
      var validFor: Duration = Duration.ofSeconds(30)
        private set
    }

    @Comment("When true, the tracked player is notified when someone starts to track them")
    var notification = true
      private set
  }

  @Comment(
      """
    Customize the used sounds
    For available names enter "/playsound" and view the suggested sounds. Technically custom sounds from resource packs can also be used
    Pitch and volume can both be customized
  """)
  var sounds = Sounds()
    private set

  @ConfigSerializable
  class Sounds {
    var openGui = Sound.sound().type(Key.key("block.ender_chest.open")).volume(0.5f).build()
      private set

    var click = Click()
      private set

    @ConfigSerializable
    class Click {
      var normal = Sound.sound().type(Key.key("ui.button.click")).volume(0.3f).build()
        private set

      var danger = Sound.sound().type(Key.key("block.lava.pop")).volume(0.75f).build()
        private set

      var dangerAbort = Sound.sound().type(Key.key("block.lava.extinguish")).volume(0.5f).build()
        private set

      var success =
          Sound.sound().type(Key.key("entity.player.levelup")).volume(0.5f).pitch(2f).build()
        private set

      var error =
          Sound.sound().type(Key.key("entity.villager.hurt")).volume(0.5f).pitch(1.5f).build()
        private set
    }

    var waypoint = Waypoint()
      private set

    @ConfigSerializable
    class Waypoint {
      var created = Sound.sound().type(Key.key("block.beacon.activate")).pitch(1.5f).build()
        private set

      var selected = Sound.sound().type(Key.key("block.beacon.power_select")).volume(0.5f).build()
        private set
    }

    var player = Player()
      private set

    @ConfigSerializable
    class Player {
      var selected = Sound.sound().type(Key.key("block.beacon.power_select")).volume(0.5f).build()
        private set

      var notification = Sound.sound().type(Key.key("entity.wither.spawn")).volume(0.1f).build()
        private set
    }

    var teleport = Sound.sound().type(Key.key("entity.enderman.teleport")).volume(0.5f).build()
      private set
  }

  @Comment("Set default colors for the beacon pointer for the different waypoint types")
  var beaconPointerDefaultColors = BeaconPointerDefaultColors()
    private set

  @ConfigSerializable
  class BeaconPointerDefaultColors {
    var private = BeaconColor.CLEAR
      private set

    @Comment("The color of the death waypoint cannot be changed ingame")
    var death = BeaconColor.RED
      private set

    var public = BeaconColor.GREEN
      private set

    var permission = BeaconColor.PURPLE
      private set

    var player = BeaconColor.PINK
      private set

    var temporary = BeaconColor.ORANGE
      private set

    fun getDefaultColor(type: Type) =
        when (type) {
          Type.PUBLIC -> public
          Type.PERMISSION -> permission
          Type.DEATH -> death
          Type.PRIVATE -> private
        }
  }

  var pointers = PointerConfiguration()
    private set
}
