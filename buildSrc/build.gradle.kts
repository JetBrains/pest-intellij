plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.intellij.platform:intellij-platform-gradle-plugin:2.16.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
}
