package de.md5lukas.waypoints.integrations

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class VaultIntegration {

    private var economy: Economy? = null

    fun setupEconomy(): Boolean {
        if (Bukkit.getPluginManager().getPlugin("Vault") === null) {
            return false
        }
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java) ?: return false

        economy = rsp.provider
        return true
    }

    fun withdraw(player: Player, amount: Double) =
        (economy ?: throw IllegalStateException("There was an attempt to withdraw money via Vault without Vault being setup"))
            .withdrawPlayer(player, amount).transactionSuccess()

    fun getBalance(player: Player) =
        (economy ?: throw IllegalStateException("There was an attempt to check the balance via Vault without Vault being setup"))
            .getBalance(player)
}