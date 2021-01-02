import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "6.1.0"

    // Apply the application plugin to add support for building a jar
    java
}

repositories {

    // Use jcenter for resolving dependencies.
    jcenter()
    mavenCentral()

    listOf(
            "repo1.maven.org/maven2",
           "repo.spongepowered.org/maven",
            "libraries.minecraft.net",
            "jitpack.io",
            "jcenter.bintray.com"
            //"https://oss.sonatype.org/content/repositories/snapshots/"
    ).forEach { maven(url = "https://$it") } // require https for all dependencies
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib"))

    // Use the Kotlin reflect library.
    implementation(kotlin("reflect"))

    // Compile Minestom into project
    //implementation("com.github.Minestom", "Minestom", "7bcca8ff9f")
    implementation("com.github.Minestom", "Minestom", "2ec727d5f8")

    // OkHttp
    implementation("com.squareup.okhttp3", "okhttp", "4.9.0")

    // org.json
    implementation("org.json", "json", "20200518")

    // KStom
    implementation("com.github.Project-Cepi:KStom:latest")

    // import kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("com.github.cliftonlabs:json-simple:3.1.0")

    implementation("com.mojang:brigadier:1.0.17")

    //implementation("com.github.mworzala:adventure-platform-minestom:f1d1c3adc5")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("fang")
        manifest {
            attributes (
                    "Main-Class" to "me.devtec.fang.Loader",
                    "Multi-Release" to true

            )
        }
    }

    test { useJUnitPlatform() }

    build { dependsOn(shadowJar) }

}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/java")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}