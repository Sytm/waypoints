@file:Suppress("UnstableApiUsage")

package de.md5lukas.waypoints.api

import com.destroystokyo.paper.profile.ProfileProperty
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.ResolvableProfile
import java.util.*
import net.kyori.adventure.key.Key
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType

sealed class Icon {
  protected abstract val item: ItemStack

  fun asItemStack(): ItemStack = item.clone()

  abstract fun asString(): String

  open fun getBytes() = item.serializeAsBytes()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Icon) return false

    return item.isSimilar(other.item)
  }

  override fun hashCode(): Int {
    return item.hashCode()
  }

  val type: ItemType
    get() = item.type.asItemType()!!

  class Default(
      private val itemType: ItemType,
      private val customModelData: String?,
  ) : Icon() {

    override val item: ItemStack =
        itemType.createItemStack().also {
          if (customModelData != null) {
            it.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA, deserializeCustomModelData(customModelData))
          }
        }

    override fun asString(): String {
      return if (customModelData == null) {
        itemType.key().asMinimalString()
      } else {
        "${itemType.key().asMinimalString()}$CUSTOM_MODEL_DATA_SEPARATOR$customModelData"
      }
    }
  }

  class PlayerHead(private val textureId: String) : Icon() {

    override val item: ItemStack =
        ItemType.PLAYER_HEAD.createItemStack().also {
          it.setData(DataComponentTypes.PROFILE, deserializeProfile(textureId))
        }

    override fun asString(): String {
      return "${ItemType.PLAYER_HEAD.key().asMinimalString()}$CUSTOM_PLAYER_HEAD_SEPARATOR${textureId}"
    }
  }

  class Serialized(private val data: ByteArray) : Icon() {

    override val item: ItemStack = ItemStack.deserializeBytes(data)

    override fun asString(): String {
      return "$BINARY_SERIALIZATION_PREFIX$${Base64.getEncoder().encodeToString(data)}"
    }

    override fun getBytes() = data
  }

  companion object {

    private const val CUSTOM_PLAYER_HEAD_SEPARATOR = ';'
    private const val CUSTOM_MODEL_DATA_SEPARATOR = '|'
    private const val BINARY_SERIALIZATION_PREFIX = '~'

    private val DATA_COMPONENT_WHITELIST =
        setOf(
            DataComponentTypes.CUSTOM_MODEL_DATA,
            DataComponentTypes.ITEM_MODEL,
            DataComponentTypes.TRIM,
            DataComponentTypes.BANNER_PATTERNS,
            DataComponentTypes.BASE_COLOR,
            DataComponentTypes.PROFILE,
            DataComponentTypes.DYED_COLOR,
        )

    fun nullableIcon(string: ByteArray?): Icon? {
      return string?.let { Serialized(it) }
    }

    fun icon(string: String): Icon {
      if (string[0] == BINARY_SERIALIZATION_PREFIX) {
        return Serialized(Base64.getDecoder().decode(string.substring(1)))
      }

      val textureIDIndex = string.indexOf(CUSTOM_PLAYER_HEAD_SEPARATOR)
      if (textureIDIndex > 0) {
        return PlayerHead(string.substring(textureIDIndex + 1))
      }

      val modelDataIndex = string.indexOf(CUSTOM_MODEL_DATA_SEPARATOR)
      var mutString = string
      var customModelData: String? = null
      if (modelDataIndex > 0) {
        customModelData = string.substring(modelDataIndex + 1)
        mutString = string.take(modelDataIndex)
      }
      return Default(Registry.ITEM.getOrThrow(Key.key(mutString)), customModelData)
    }

    fun icon(item: ItemStack): Icon {
      return Serialized(sanitizeItem(item).serializeAsBytes())
    }

    private fun sanitizeItem(item: ItemStack): ItemStack {
      val sanitized = ItemStack.of(item.type)

      sanitized.copyDataFrom(item) { it in DATA_COMPONENT_WHITELIST }

      return sanitized
    }

    private fun deserializeCustomModelData(jsonString: String): CustomModelData.Builder {
      val jsonObject =
          JsonParser.parseReader(
                  JsonReader(jsonString.reader()).also { jsonReader ->
                    jsonReader.strictness = Strictness.LENIENT
                  })
              .asJsonObject
      val builder = CustomModelData.customModelData()

      (jsonObject["floats"] as? JsonArray)?.let { floats ->
        floats.forEach { builder.addFloat(it.asFloat) }
      }
      (jsonObject["flags"] as? JsonArray)?.let { flags ->
        flags.forEach { builder.addFlag(it.asBoolean) }
      }
      (jsonObject["strings"] as? JsonArray)?.let { strings ->
        strings.forEach { builder.addString(it.asString) }
      }
      (jsonObject["colors"] as? JsonArray)?.let { colors ->
        colors.forEach {
          it.asJsonPrimitive.let { color ->
            if (color.isString) {
              val str = color.asString
              if (str.startsWith('#')) {
                builder.addColor(Color.fromRGB(str.substring(1).toInt(16)))
                return@forEach
              }
            }
            builder.addColor(Color.fromRGB(color.asInt))
          }
        }
      }

      return builder
    }

    @Suppress("HttpUrlsUsage")
    fun deserializeProfile(textureID: String): ResolvableProfile.Builder {
      val fullUrl = "http://textures.minecraft.net/texture/$textureID"

      val json =
          JsonObject()
              .also { root ->
                root.add(
                    "textures",
                    JsonObject().also { textures ->
                      textures.add(
                          "SKIN", JsonObject().also { skin -> skin.addProperty("url", fullUrl) })
                    })
              }
              .toString()

      return ResolvableProfile.resolvableProfile()
          .name("CUSTOM_HEAD")
          .uuid(UUID.randomUUID())
          .addProperty(
              ProfileProperty(
                  "textures", Base64.getEncoder().encodeToString(json.encodeToByteArray())))
    }
  }
}
