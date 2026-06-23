import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.intellij.platform.gradle.ProductMode
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformDependenciesExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.prop(key: String): String = providers.gradleProperty(key).getOrElse("")

fun IntelliJPlatformDependenciesExtension.platform(project: Project) {
    create(project.providers.gradleProperty("platformType"), project.providers.gradleProperty("platformVersion")) {
        useInstaller.set(false)
        productMode.set(ProductMode.MONOLITH)
    }
    testFramework(TestFrameworkType.Platform)

    bundledPlugin("com.jetbrains.php")
    bundledPlugin("intellij.libraries.misc.plugin")
    bundledPlugin("intellij.structureView.plugin")
    bundledPlugin("intellij.testRunner.plugin")
    bundledPlugin("intellij.structuralSearch.plugin")
    bundledModule("intellij.platform.coverage")
}

fun Project.configureJvm() {
    val javaVersion = prop("javaVersion")
    configure<KotlinJvmProjectExtension> {
        jvmToolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion)) }
        compilerOptions { jvmTarget.set(JvmTarget.fromTarget(javaVersion)) }
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}