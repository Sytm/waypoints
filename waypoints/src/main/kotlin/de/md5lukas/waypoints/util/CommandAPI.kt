package de.md5lukas.waypoints.util

import de.md5lukas.commons.paper.placeholder
import dev.jorel.commandapi.executors.CommandArguments
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

val CommandArguments.labelResolver: TagResolver
  get() = "label" placeholder fullInput.substringBefore(' ').removePrefix("/")
