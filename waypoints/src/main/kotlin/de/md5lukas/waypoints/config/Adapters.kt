package de.md5lukas.waypoints.config

import de.md5lukas.commons.time.DurationParser
import de.md5lukas.konfig.RegisteredTypeAdapter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object MaterialListAdapter : RegisteredTypeAdapter<List<Material>> {
    override fun get(section: ConfigurationSection, path: String) =
        section.getStringList(path)
            .map { Material.matchMaterial(it) ?: throw IllegalArgumentException("The material $it is not valid") }

    override fun isApplicable(clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>) =
        clazz.isSubclassOf(List::class) && typeArgumentClasses.firstOrNull() == Material::class
}

object BlockDataAdapter : RegisteredTypeAdapter<BlockData> {
    override fun get(section: ConfigurationSection, path: String) =
        section.getString(path)
            ?.let { Material.matchMaterial(it) ?: throw IllegalArgumentException("The material $it is not valid") }
            ?.let { Bukkit.createBlockData(it) }

    override fun isApplicable(clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>) =
        clazz == BlockData::class
}

object DurationAdapter : RegisteredTypeAdapter.Static<Duration>(Duration::class) {
    override fun get(section: ConfigurationSection, path: String) =
        section.getString(path)?.let {
            Duration.ofMillis(DurationParser.parseDuration(it, TimeUnit.MILLISECONDS))
        }
}