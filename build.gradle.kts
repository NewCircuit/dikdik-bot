import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.newcircuit"
version = "2.0.0"

plugins {
    kotlin("jvm") version "1.5.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.0"
    application
    distribution
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javacord:javacord:3.3.0")
    implementation("com.charleskorn.kaml:kaml:0.33.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.snakeyaml:snakeyaml-engine:2.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.0")
}

application {
    mainClass.set("MainKt")
}

distributions {
    main {
        contents {
            from("./config") {
                into("config")
            }
            from("./README.md", "LICENSE")
        }
    }
}
