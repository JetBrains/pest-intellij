import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog") version "2.2.0"
}

group = prop("pluginGroup")
version = prop("pluginVersion")

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("io.mockk:mockk:1.14.3") {
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm")
    }
    testImplementation("junit:junit:4.13.2")
    intellijPlatform {
        platform(project)

        // Compose the :coverage content module into lib/modules/; runtimeOnly avoids a compile cycle
        // (the main module never references coverage, but coverage depends back on it).
        pluginModule(runtimeOnly(project(":coverage")))
    }
}

// Share the main test classes with the :coverage subproject's tests.
configurations.create("testClassesExport") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(tasks.register<Jar>("testClassesJar") {
        description = "test classes for the main module"
        archiveClassifier = "test"
        from(sourceSets.test.get().output)
    })
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set(prop("pluginVersion"))
    groups.set(emptyList())
}

configureJvm()

tasks {
    test {
        systemProperty("idea.home.path", projectDir.absolutePath)
    }

    wrapper {
        gradleVersion = prop("gradleVersion")
    }

    patchPluginXml {
        version = prop("pluginVersion")
        sinceBuild.set(prop("pluginSinceBuild"))
        untilBuild.set(prop("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider {
            changelog.renderItem(changelog.run {
                getOrNull(prop("pluginVersion")) ?: getLatest()
            }, Changelog.OutputType.HTML)
        })
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        // channels = listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first())
    }
}