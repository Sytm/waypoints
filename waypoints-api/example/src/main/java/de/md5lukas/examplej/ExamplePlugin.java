package de.md5lukas.examplej;

import de.md5lukas.waypoints.api.*;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ExamplePlugin extends JavaPlugin {

  private WaypointsAPI api;

  @Override
  public void onEnable() {
    api = getServer().getServicesManager().load(WaypointsAPI.class);
    if (api == null) {
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    JWaypointHolder.getAllWaypoints(api.getPublicWaypoints())
        .thenAccept(waypoints -> {
          for (Waypoint waypoint : waypoints) {
            System.out.println("Public waypoint " + waypoint.getName() + " is at position " + waypoint.getLocation());
          }
        });
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player player) {
      JWaypointsAPI.getWaypointPlayer(api, player.getUniqueId()).thenCompose(
          JWaypointsPlayer::getAllWaypoints
      ).thenAccept(waypoints -> {
        for (Waypoint waypoint : waypoints) {
          Folder folder = JWaypoint.getFolder(waypoint).join();
          player.sendMessage(Component.text("You have the waypoint " + waypoint.getName() + " in the folder " + (folder == null ? "none" : folder.getName())));
        }
      });
    }
    return true;
  }
}
