plugins {
    kotlin("jvm") version "1.9.0" // Версия Kotlin
    application
}

repositories {
    mavenCentral() // Репозиторий для зависимостей
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20") // TornadoFX
    implementation("org.openjfx:javafx-controls:21") // JavaFX (для TornadoFX)
}

application {
    mainClass.set("MainAppKt") // Главный класс
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17" // Версия JVM
}