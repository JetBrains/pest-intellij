plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform.module")
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
        platform(project)

        localPlugin(project(":"))
    }
}

configureJvm()

tasks.test {
    systemProperty("idea.home.path", rootDir.absolutePath)
    workingDir = file("tests")
}
