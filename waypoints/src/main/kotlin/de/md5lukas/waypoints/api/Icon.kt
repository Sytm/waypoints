@file:Suppress("UnstableApiUsage")

package de.md5lukas.waypoints.api

import com.destroystokyo.paper.profile.ProfileProperty
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.md5lukas.waypoints.util.Items
import de.md5lukas.waypoints.util.getValue
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
  private var cached: ItemStack? = null

  fun asItemStack(): ItemStack {
    cached?.let {
      return it.clone()
    }
    val item = asItemStack0()
    cached = item
    return item.clone()
  }

  protected abstract fun asItemStack0(): ItemStack

  abstract fun asString(): String

  class PlayerHead(private val textureId: String) : Icon() {
    override fun asItemStack0(): ItemStack {
      val item = Items.PLAYER_HEAD.getValue().createItemStack()

      item.setData(DataComponentTypes.PROFILE, deserializeProfile(textureId))

      return item
    }

    override fun asString(): String {
      return "${Items.PLAYER_HEAD.key().asMinimalString()}$CUSTOM_PLAYER_HEAD_SEPARATOR${textureId}"
    }
  }

  class CustomModelData(
      private val itemType: ItemType,
      private val customModelData: String?,
  ) : Icon() {

    override fun asItemStack0(): ItemStack {
      val item = itemType.createItemStack()

      if (customModelData != null) {
        item.setData(
            DataComponentTypes.CUSTOM_MODEL_DATA, deserializeCustomModelData(customModelData))
      }

      return item
    }

    override fun asString(): String {
      return if (customModelData == null) {
        itemType.key().asMinimalString()
      } else {
        "${itemType.key().asMinimalString()}$CUSTOM_MODEL_DATA_SEPARATOR$customModelData"
      }
    }
  }

  class Serialized(private val data: String) : Icon() {
    override fun asItemStack0(): ItemStack {
      return ItemStack.deserializeBytes(Base64.getDecoder().decode(data))
    }

    override fun asString(): String {
      return "$BINARY_SERIALIZATION_PREFIX$data"
    }
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
        )

    fun nullableIcon(string: String?): Icon? {
      return string?.let { icon(string) }
    }

    fun icon(string: String): Icon {
      if (string[0] == BINARY_SERIALIZATION_PREFIX) {
        return Serialized(string.substring(1))
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
      return CustomModelData(Registry.ITEM.getOrThrow(Key.key(mutString)), customModelData)
    }

    fun icon(item: ItemStack): Icon {
      return Serialized(Base64.getEncoder().encodeToString(sanitizeItem(item).serializeAsBytes()))
    }

    private fun sanitizeItem(item: ItemStack): ItemStack {
      val sanitized = ItemStack.of(item.type)

      sanitized.copyDataFrom(item) { it in DATA_COMPONENT_WHITELIST }

      return sanitized
    }

    private fun deserializeCustomModelData(jsonString: String): CustomModelData.Builder {
      val jsonObject = JsonParser.parseString(jsonString).asJsonObject
      val builder = io.papermc.paper.datacomponent.item.CustomModelData.customModelData()

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
        colors.forEach { builder.addColor(Color.fromRGB(it.asInt)) }
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
