package com.pestphp.pest.features.customExpectations

import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.DumbModeTestUtils
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.junit5.fixture.tempPathFixture
import com.jetbrains.php.composer.ComposerDataService
import com.jetbrains.php.composer.lib.ComposerLibraryServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@TestApplication
class CustomExpectationRemoveGeneratedFileStartupActivityTest {
    companion object {
        private val pathFixture = tempPathFixture()
        private val projectFixture = projectFixture(pathFixture, openAfterCreation = true)
    }

    private val root by pathFixture
    private val project by projectFixture

    @Test
    fun `generated file is deleted only after dumb mode ends`() = timeoutRunBlocking {
        val composerJson = Files.writeString(root.resolve("composer.json"), "{}")
        LocalFileSystem.getInstance().refreshAndFindFileByNioFile(composerJson)
        withContext(Dispatchers.EDT) {
            ComposerDataService.getInstance(project).setConfigPathAndLibraryUpdateStatus(composerJson.invariantSeparatorsPathString, false)
        }
        val generatedFile = root.resolve("vendor/Expectation.php")
        Files.createDirectories(generatedFile.parent)
        Files.writeString(generatedFile, "<?php\n\nnamespace Pest;\n\n/**\n */\nfinal class Expectation {}")
        LocalFileSystem.getInstance().refreshAndFindFileByNioFile(generatedFile)
        val libraryService = ComposerLibraryServiceFactory.getInstance(project, null as VirtualFile?)
        readAction { libraryService.reloadLibraries(false, emptySet(), false, false) }
        assertNotNull(libraryService.vendorDir?.findChild("Expectation.php"))

        val dumbTask = DumbModeTestUtils.startEternalDumbModeTask(project)
        val activity = launch(Dispatchers.Default) {
            CustomExpectationRemoveGeneratedFileStartupActivity().execute(project)
        }
        try {
            delay(2.seconds)
            assertTrue(Files.exists(generatedFile), "The file was deleted during dumb mode")
        }
        finally {
            DumbModeTestUtils.endEternalDumbModeTaskAndWaitForSmartMode(project, dumbTask)
        }
        activity.join()

        assertFalse(Files.exists(generatedFile), "The file was not deleted after dumb mode")
    }

    @Test
    fun `config path change during dumb mode deletes the generated file only after dumb mode ends`() = timeoutRunBlocking {
        val composerJson = Files.writeString(root.resolve("composer.json"), "{}")
        LocalFileSystem.getInstance().refreshAndFindFileByNioFile(composerJson)
        val composerPath = composerJson.invariantSeparatorsPathString
        withContext(Dispatchers.EDT) {
            ComposerDataService.getInstance(project).setConfigPathAndLibraryUpdateStatus(composerPath, false)
        }
        val generatedFile = root.resolve("vendor/Expectation.php")
        Files.createDirectories(generatedFile.parent)
        Files.deleteIfExists(generatedFile)
        LocalFileSystem.getInstance().refreshAndFindFileByNioFile(generatedFile.parent)
        val libraryService = ComposerLibraryServiceFactory.getInstance(project, null as VirtualFile?)
        readAction { libraryService.reloadLibraries(false, emptySet(), false, false) }
        CustomExpectationRemoveGeneratedFileStartupActivity().execute(project)

        Files.writeString(generatedFile, "<?php\n\nnamespace Pest;\n\n/**\n */\nfinal class Expectation {}")
        LocalFileSystem.getInstance().refreshAndFindFileByNioFile(generatedFile)
        assertNotNull(libraryService.vendorDir?.findChild("Expectation.php"))

        val dumbTask = DumbModeTestUtils.startEternalDumbModeTask(project)
        try {
            ComposerDataService.getInstance(project).configPathChanged(null, composerPath)
            delay(2.seconds)
            assertTrue(Files.exists(generatedFile), "The file was deleted during dumb mode")
        }
        finally {
            DumbModeTestUtils.endEternalDumbModeTaskAndWaitForSmartMode(project, dumbTask)
        }

        repeat(100) {
            if (!Files.exists(generatedFile)) return@timeoutRunBlocking
            delay(100.milliseconds)
        }
        fail<Unit>("The file was not deleted after dumb mode")
    }
}
