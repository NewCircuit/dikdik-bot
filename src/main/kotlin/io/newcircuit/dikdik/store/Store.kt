package io.newcircuit.dikdik.store

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

@Serializable
abstract class Store<T>(@Transient protected val location: String = "") {
    fun init() {
        val file = File(location)
        if (file.exists()) {
            load(file)
        } else {
            generate(file)
        }
    }

    protected abstract fun generate(file: File)

    protected abstract fun load(file: File)

    abstract suspend fun save(data: T)
}
