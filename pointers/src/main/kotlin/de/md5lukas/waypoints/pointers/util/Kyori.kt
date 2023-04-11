package de.md5lukas.waypoints.pointers.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

internal inline fun textComponent(builder: TextComponent.Builder.() -> Unit): TextComponent = Component.text().also { it.builder() }.build()