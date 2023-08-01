package de.md5lukas.waypoints.config

import de.md5lukas.konfig.RegisteredTypeAdapter
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.time.toJavaDuration
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection

object MaterialListAdapter : RegisteredTypeAdapter<List<Material>> {
  override fun get(section: ConfigurationSection, path: String) =
      section.getStringList(path).map {
        Material.matchMaterial(it)
            ?: throw IllegalArgumentException("The material $it is not valid")
      }

  override fun isApplicable(clazz: KClass<*>, typeArgumentClasses: List<KClass<*>>) =
      clazz.isSubclassOf(List::class) && typeArgumentClasses.firstOrNull() == Material::class
}

object BlockDataAdapter : RegisteredTypeAdapter.Static<BlockData>(BlockData::class) {

  override fun get(section: ConfigurationSection, path: String) =
      section
          .getString(path)
          ?.let {
            Material.matchMaterial(it)
                ?: throw IllegalArgumentException("The material $it is not valid")
          }
          ?.let { Bukkit.createBlockData(it) }
}

object DurationAdapter : RegisteredTypeAdapter.Static<Duration>(Duration::class) {
  override fun get(section: ConfigurationSection, path: String) =
      section.getString(path)?.let { kotlin.time.Duration.parse(it).toJavaDuration() }
}

object StyleAdapter : RegisteredTypeAdapter.Static<Style>(Style::class) {

  override fun get(section: ConfigurationSection, path: String) =
      section.getString(path)?.let { MiniMessage.miniMessage().deserialize(it).style() }
}

object SoundAdapter : RegisteredTypeAdapter.Static<Sound>(Sound::class) {

  override fun get(section: ConfigurationSection, path: String) =
      section.getString("$path.name")?.let {
        Sound.sound()
            .type(Key.key(it))
            .volume(section.getDouble("$path.volume", 1.0).toFloat())
            .pitch(section.getDouble("$path.pitch", 1.0).toFloat())
            .build()
      }
}
