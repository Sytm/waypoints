package de.md5lukas.waypoints.api

import com.destroystokyo.paper.profile.ProfileProperty
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.ResolvableProfile
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID
import net.kyori.adventure.key.Key
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.jetbrains.annotations.VisibleForTesting

@Suppress("UnstableApiUsage")
data class Icon
@VisibleForTesting
internal constructor(val item: ItemType, val customModelData: String?, val textureID: String?) {

  private var cached: ItemStack? = null

  fun asItemStack(): ItemStack {
    cached?.let {
      return it.clone()
    }
    val item = item.createItemStack()

    if (customModelData != null) {
      item.setData(
          DataComponentTypes.CUSTOM_MODEL_DATA, deserializeCustomModelData(customModelData))
    }
    if (textureID != null) {
      item.setData(DataComponentTypes.PROFILE, deserializeProfile(textureID))
    }

    cached = item

    return item.clone()
  }

  fun asString(): String {
    return buildString {
      append(item.key.asMinimalString())
      if (textureID != null) {
        append(CUSTOM_PLAYER_HEAD_SEPARATOR)
        append(textureID)
      }
      if (customModelData != null) {
        append(CUSTOM_MODEL_DATA_SEPARATOR)
        append(customModelData)
      }
    }
  }

  companion object {

    private const val CUSTOM_MODEL_DATA_SEPARATOR = '|'
    private const val CUSTOM_PLAYER_HEAD_SEPARATOR = ';'

    fun nullableIcon(string: String?): Icon? {
      return string?.let { icon(string) }
    }

    fun icon(string: String): Icon {
      var mutString = string
      var customModelData: String? = null
      var textureID: String? = null

      val modelDataIndex = mutString.indexOf(CUSTOM_MODEL_DATA_SEPARATOR)
      if (modelDataIndex > 0) {
        customModelData = mutString.substring(modelDataIndex + 1)
        mutString = mutString.substring(0, modelDataIndex)
      }
      val textureIDIndex = mutString.indexOf(CUSTOM_PLAYER_HEAD_SEPARATOR)
      if (textureIDIndex > 0) {
        textureID = mutString.substring(textureIDIndex + 1)
        mutString = mutString.substring(0, textureIDIndex)
      }
      return Icon(Registry.ITEM.getOrThrow(Key.key(mutString)), customModelData, textureID)
    }

    fun icon(item: ItemStack): Icon {
      var customModelData: String? = null
      var textureID: String? = null
      if (item.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
        val data = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA)
        if (data != null) {
          customModelData = serializeCustomModelData(data)
        }
      }
      if (item.hasData(DataComponentTypes.PROFILE)) {
        val data = item.getData(DataComponentTypes.PROFILE)
        if (data != null) {
          textureID = serializeProfile(data)
        }
      }
      return Icon(item.type.asItemType()!!, customModelData, textureID)
    }

    private fun serializeCustomModelData(customModelData: CustomModelData): String {
      val jsonObject = JsonObject()

      if (customModelData.floats().isNotEmpty()) {
        val floats = JsonArray()
        customModelData.floats().forEach { floats.add(it) }
        jsonObject.add("floats", floats)
      }
      if (customModelData.flags().isNotEmpty()) {
        val flags = JsonArray()
        customModelData.flags().forEach { flags.add(it) }
        jsonObject.add("flags", flags)
      }
      if (customModelData.strings().isNotEmpty()) {
        val strings = JsonArray()
        customModelData.strings().forEach { strings.add(it) }
        jsonObject.add("strings", strings)
      }
      if (customModelData.colors().isNotEmpty()) {
        val floats = JsonArray()
        customModelData.colors().forEach { floats.add(it.asRGB()) }
        jsonObject.add("colors", floats)
      }

      return jsonObject.toString()
    }

    private fun deserializeCustomModelData(jsonString: String): CustomModelData.Builder {
      val jsonObject = JsonParser.parseString(jsonString).asJsonObject
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
        colors.forEach { builder.addColor(Color.fromRGB(it.asInt)) }
      }

      return builder
    }

    private fun serializeProfile(data: ResolvableProfile): String? {
      val texturesBase64 =
          data.properties().firstOrNull { it.name == "textures" }?.value ?: return null
      val texturesJson = Base64.getDecoder().decode(texturesBase64).toString(StandardCharsets.UTF_8)
      val textureUrl =
          JsonParser.parseString(texturesJson)
              .asJsonObject
              .getAsJsonObject("textures")
              ?.getAsJsonObject("SKIN")
              ?.getAsJsonPrimitive("url")
              ?.asString ?: return null
      return textureUrl.substringAfterLast('/')
    }

    @Suppress("HttpUrlsUsage")
    private fun deserializeProfile(textureID: String): ResolvableProfile.Builder {
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
