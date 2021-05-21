package io.newcircuit.dikdik.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import java.io.File

internal const val CONFIG_ROOT = "./config"

private val ENCODING = Charsets.UTF_8

internal fun <T> getConfig(
    location: String,
    ref: T,
    serializer: SerializationStrategy<T>,
    deserializer: DeserializationStrategy<T>,
): T {
    val configFile = File(location)

    if (!configFile.exists()) {
        genConfig(
            configFile,
            ref = ref,
            serializer = serializer,
        )
    }

    val configStr = configFile.readText(ENCODING)
    return Yaml.default.decodeFromString(deserializer, configStr)
}

internal fun <T> genConfig(
    file: File,
    ref: T,
    serializer: SerializationStrategy<T>,
) {
    val serialized = Yaml.default.encodeToString(serializer, ref)

    file.writeText(serialized, ENCODING)

    throw Exception("New default config generated")
}
