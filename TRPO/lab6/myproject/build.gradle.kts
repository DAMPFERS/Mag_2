plugins {
    // kotlin("jvm") version "2.1.20"  // Версия Kotlin из вашего вывода
    kotlin("jvm") version "1.9.23"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")  // TornadoFX для Kotlin 2.x
}

kotlin {
    jvmToolchain(21)  // Явное указание Java 21
}

javafx {
    version = "21"     // Версия JavaFX должна совпадать с JDK
    modules = listOf("javafx.controls", "javafx.graphics")
}

application {
    //mainClass.set("com.example.MainKt")
    mainClass.set("com.example.app.MainApp") 
}