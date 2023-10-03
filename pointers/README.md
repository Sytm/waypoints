# pointers
![](https://repo.md5lukas.de/api/badge/latest/releases/de/md5lukas/waypoints/pointers)
![](https://img.shields.io/github/license/Sytm/waypoints)  

Use the direction indicators of the Waypoints plugin in your own projects.

## Setting up the dependency

Do not forget to properly shade the library!
```kotlin
repositories {
  maven("https://repo.md5lukas.de/releases")
}

dependencies {
  implementation("de.md5lukas.waypoints:pointers:<version>")
}
```

## Using the pointers library:
At first we need to create classes implementing `PointerManager.Hooks` and `PointerConfiguration`.
- `PointerManager.Hooks` is primarily used for persistence of selected trackables and the compass target and for formatting of the text for the ActionBar pointer
- `PointerConfiguration` serves as a definition of an expected configuration layout. Either hardcode the values, use the Bukkit configuration API or something like Configurate

Now we can create a `PointerManager` instance, which is the primary API surface of this library:
```kotlin
val manager = PointerManager(
  plugin, // Your plugin instance, used for scheduling and event registration
  hooks,
  config,
)
```

Now you can start using the `PointerManager` to show pointers to different locations.
For that you need to either implement `Trackable` for moving targets or `StaticTrackable` for immobile targets and then show them to the player.
```kotlin
manager.enable(player, trackable)
```

To disable a trackable a `TrackablePredicate` is required.

To disable all trackables:
```kotlin
manager.disable(player) { true }
```

To disable a certain trackable you still have the instance of:
```kotlin
val trackable: Trackable = ...

manager.disable(player, trackable.asPredicate())
```

To disable trackables in a certain world
```kotlin
manager.disable(player) {
  it.location.world == someWorld
}
```

If a trackable has been deleted which can be selected by multiple players:
```kotlin
val trackable: Trackable = ...

manager.disableAll(trackable.asPredicate())
```
This disables the trackable for every player that has it selected