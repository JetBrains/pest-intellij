import org.jetbrains.intellij.platform.gradle.ProductMode
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform.module")
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("tests/src")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly(project(":"))

    testCompileOnly(project(":"))
    testImplementation(project(path = ":", configuration = "testClassesExport"))
    testImplementation("io.mockk:mockk:1.14.3") {
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm")
    }
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion")) {
            useInstaller = false
            productMode = ProductMode.MONOLITH
        }
        bundledPlugins(properties("platformBundledPlugins").toPlugins())
        bundledModules(properties("platformBundledModules").toPlugins())
        testFramework(TestFrameworkType.Platform)

        localPlugin(project(":"))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(providers.gradleProperty("javaVersion").get()))
    }
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(providers.gradleProperty("javaVersion").get()))
    }
}

tasks.withType<JavaCompile>().configureEach {
    val javaVersion = properties("javaVersion")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.test {
    systemProperty("idea.home.path", rootDir.absolutePath)
    workingDir = file("tests")
}

private fun String.toPlugins(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotEmpty)
