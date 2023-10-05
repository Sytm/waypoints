package de.md5lukas.waypoints.integrations

import de.md5lukas.waypoints.WaypointsPlugin
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player

class VaultIntegration(private val plugin: WaypointsPlugin) {

  private var economy: Economy? = null

  @Suppress("UnstableApiUsage")
  fun setupEconomy(): Boolean {
    if ("Vault" !in plugin.pluginMeta.pluginSoftDependencies ||
        plugin.server.pluginManager.getPlugin("Vault") === null) {
      return false
    }
    val rsp = plugin.server.servicesManager.getRegistration(Economy::class.java) ?: return false

    economy = rsp.provider
    return true
  }

  fun withdraw(player: Player, amount: Double) =
      (economy
              ?: throw IllegalStateException(
                  "There was an attempt to withdraw money via Vault without Vault being setup"))
          .withdrawPlayer(player, amount)
          .transactionSuccess()

  fun deposit(player: Player, amount: Double) =
      (economy
              ?: throw IllegalStateException(
                  "There was an attempt to withdraw money via Vault without Vault being setup"))
          .depositPlayer(player, amount)
          .transactionSuccess()

  fun getBalance(player: Player) =
      (economy
              ?: throw IllegalStateException(
                  "There was an attempt to check the balance via Vault without Vault being setup"))
          .getBalance(player)
}
