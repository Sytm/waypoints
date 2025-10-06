package de.md5lukas.configurate

import de.md5lukas.configurate.serializers.BlockDataSerializer
import de.md5lukas.configurate.serializers.BlockTypeSerializer
import de.md5lukas.configurate.serializers.ComponentSerializer
import de.md5lukas.configurate.serializers.DurationSerializer
import de.md5lukas.configurate.serializers.ItemTypeSerializer
import de.md5lukas.configurate.serializers.PeriodSerializer
import de.md5lukas.configurate.serializers.SoundSerializer
import de.md5lukas.configurate.serializers.StyleSerializer
import java.time.Period
import net.kyori.adventure.sound.Sound
import org.spongepowered.configurate.kotlin.extensions.addConstraint
import org.spongepowered.configurate.kotlin.extensions.addProcessor
import org.spongepowered.configurate.kotlin.kotlinCommentsProcessor
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.util.NamingScheme
import org.spongepowered.configurate.util.NamingSchemes

fun commonSerializers(
    namingScheme: NamingScheme = NamingSchemes.CAMEL_CASE
): TypeSerializerCollection =
    TypeSerializerCollection.builder()
        .register(BlockDataSerializer)
        .register(BlockTypeSerializer)
        .register(DurationSerializer)
        .register(ItemTypeSerializer)
        .register(Period::class.java, PeriodSerializer)
        .register(Sound::class.java, SoundSerializer)
        .register(StyleSerializer)
        .register(ComponentSerializer)
        .registerAnnotatedObjects(
            ObjectMapper.factoryBuilder()
                .defaultNamingScheme(namingScheme)
                .addConstraint(Positive.Factory)
                .addConstraint(Min.Factory)
                .addConstraint(Max.Factory)
                .addConstraint(NonEmptyString.Factory)
                .addProcessor(kotlinCommentsProcessor())
                .build())
        .build()
