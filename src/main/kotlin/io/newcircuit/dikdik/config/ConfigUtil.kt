package io.newcircuit.dikdik.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import java.io.File

val ENCODING = Charsets.UTF_8

fun <T> getConfig(
    location: String,
    ref: T,
    serializer: SerializationStrategy<T>,
    deserializer: DeserializationStrategy<T>,
): T {
    val configFile = File(location)

    return getConfig(
        file = configFile,
        ref,
        serializer,
        deserializer,
    )
}

fun <T> getConfig(
    file: File,
    ref: T,
    serializer: SerializationStrategy<T>,
    deserializer: DeserializationStrategy<T>,
): T {
    if (!file.exists()) {
        genConfig(file, ref, serializer)
    }

    val configFileStr = file.readText(ENCODING)
    return Yaml.default.decodeFromString(deserializer, configFileStr)
}

fun <T> genConfig(
    file: File,
    ref: T,
    serializer: SerializationStrategy<T>,
) {
    val serialized = Yaml.default.encodeToString(serializer, ref)

    file.writeText(serialized, ENCODING)

    throw Exception("New default config generated")
}
