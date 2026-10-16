package com.pestphp.pest.features.parallel

import com.intellij.execution.ExecutionException
import com.intellij.execution.PsiLocation
import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiElement
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.composer.ComposerDataService
import com.jetbrains.php.composer.configData.ComposerConfigManager
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import com.pestphp.pest.configuration.PestVersionDetector
import com.pestphp.pest.PEST_FRAMEWORK_TYPE
import com.pestphp.pest.configuration.PEST_RUN_CONFIGURATION_TYPE
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/parallel")
class PestParallelProgramRunnerTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var testMethodName: String
    private lateinit var mockDetector: PestVersionDetector

    private data class MultiComposerPaths(
        val rootExecutablePath: String,
        val subprojectExecutablePath: String,
        val subprojectTestFilePath: String,
    )

    @Test
    fun testCannotRunWrongExecutorId(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertFalse(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID + "1", configuration))
    }

    @Test
    fun testCanRunFile(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    @Test
    fun testCanRunFunction(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.firstChild?.lastChild?.firstChild } ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    @Test
    fun testCanRunDirectory(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.containingDirectory } ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    @Test
    fun testBuildFile(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)

        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout ${configuredFilePath()}"
        assertEquals(expected, pestParallelCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testBuildFunction(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.firstChild?.lastChild?.firstChild } ?: return@doTest
        val configuration = createConfiguration(testElement)

        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout ${configuredFilePath()}"
        assertEquals(
            expected,
            pestParallelCommandSettings.createGeneralCommandLine().parametersList.list
                .joinToString(" ")
                .substringBefore(" --filter")
        )
    }

    @Test
    fun testBuildDirectory(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.containingDirectory } ?: return@doTest
        val configuration = createConfiguration(testElement)
        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout ${configuredFilePath().substringBeforeLast('/')}"
        assertEquals(expected, pestParallelCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testBuildWithParaTestModeWhenVersionAtLeast4_6_0(): Unit = doTest {
        every { mockDetector.getVersion(any(), any(), any()) } returns "4.6.0"
        val configuration = createConfiguration(myFixture.file)

        val expected = "randomPath --teamcity --parallel ${configuredFilePath()}"
        assertEquals(expected, createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testBuildWithLegacyModeWhenVersionBelow4_6_0(): Unit = doTest {
        every { mockDetector.getVersion(any(), any(), any()) } returns "4.5.0"
        val configuration = createConfiguration(myFixture.file)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout ${configuredFilePath()}"
        assertEquals(expected, createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testBuildWithParaTestModeUsesConfigurationFromBaseFileComposer(): Unit = doTest {
        withMultiComposerConfigurations { paths ->
            every { mockDetector.getVersion(project, any(), paths.rootExecutablePath) } returns "4.5.0"
            every { mockDetector.getVersion(project, any(), paths.subprojectExecutablePath) } returns "4.6.0"

            val configuration = createConfiguration(myFixture.file, withDefaultFrameworkConfiguration = false)
            configuration.settings.runnerSettings.filePath = paths.subprojectTestFilePath
            val commandLine = createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" ")

            val expected = "${paths.subprojectExecutablePath} --teamcity --parallel ${paths.subprojectTestFilePath}"
            assertEquals(expected, commandLine)
            verify(exactly = 1) { mockDetector.getVersion(project, configuration.interpreter!!, paths.subprojectExecutablePath) }
            verify(exactly = 0) { mockDetector.getVersion(project, configuration.interpreter!!, paths.rootExecutablePath) }
        }
    }

    private fun createConfiguration(psiElement: PsiElement, withDefaultFrameworkConfiguration: Boolean = true): PestRunConfiguration {
        if (withDefaultFrameworkConfiguration) {
            PestTestUtil.createPestFrameworkConfiguration(project)
        }
        val runConfiguration = runReadActionBlocking {
            val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(psiElement))
            PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        }
        runConfiguration!!.settings.commandLineSettings.interpreterSettings.interpreterName = PlatformTestUtil.getTestName(testMethodName, false)
        return runConfiguration
    }

    private inline fun withMultiComposerConfigurations(block: (MultiComposerPaths) -> Unit) {
        val composerDataService = ComposerDataService.getInstance(project)
        val composerConfigManager = ComposerConfigManager.getInstance(project)
        val oldConfigPath = composerConfigManager.getMainConfigPath()
        val oldUpdateLibrary = composerDataService.isUpdateLibrary
        val oldSubProjectConfigs = composerConfigManager.getSubProjectConfigs().toList()

        try {
            block(createMultiComposerConfigurations())
        }
        finally {
            onEdt {
                composerDataService.setConfigPathAndLibraryUpdateStatus(oldConfigPath, oldUpdateLibrary)
                composerConfigManager.setSubProjectConfigs(oldSubProjectConfigs)
            }
        }
    }

    private fun createMultiComposerConfigurations(): MultiComposerPaths {
        val root = StandardFileSystems.local().refreshAndFindFileByPath("${myFixture.testDataPath}/multiComposer")!!
        val interpreter = PhpInterpretersManagerImpl.getInstance(project).interpreters.single()
        val settingsManager = PhpTestFrameworkSettingsManager.getInstance(project)
        val rootExecutable = root.findFileByRelativePath("vendor/bin/pest")!!
        val subprojectExecutable = root.findFileByRelativePath("subproject/vendor/bin/pest")!!
        val subprojectTestFile = root.findFileByRelativePath("subproject/tests/ATest.php")!!
        val rootComposer = root.findFileByRelativePath("composer.json")!!
        val subprojectComposer = root.findFileByRelativePath("subproject/composer.json")!!

        onEdt {
            ComposerDataService.getInstance(project).setConfigPathAndLibraryUpdateStatus(rootComposer.path, true)
            ComposerConfigManager.getInstance(project).addConfig(subprojectComposer)
        }

        val rootConfiguration = settingsManager
            .getOrCreateByInterpreter(PEST_FRAMEWORK_TYPE, interpreter, rootComposer, false)
            ?.apply { executablePath = rootExecutable.path }
        val subprojectConfiguration = settingsManager
            .getOrCreateByInterpreter(PEST_FRAMEWORK_TYPE, interpreter, subprojectComposer, false)
            ?.apply { executablePath = subprojectExecutable.path }

        settingsManager.setConfigurations(
            PEST_FRAMEWORK_TYPE,
            listOfNotNull(rootConfiguration, subprojectConfiguration)
        )

        return MultiComposerPaths(rootExecutable.path, subprojectExecutable.path, subprojectTestFile.path)
    }

    private fun onEdt(block: () -> Unit): Unit = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            block()
        }
    }

    private fun configuredFilePath(): String = runReadActionBlocking { myFixture.file.virtualFile.path }

    private fun doTest(block: () -> Unit) {
        myFixture.configureByFile("ATest.php")
        block()
    }

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/parallel")
        testMethodName = testInfo.testMethod.get().name
        val interpreter = PhpInterpreter().apply {
            name = PlatformTestUtil.getTestName(testMethodName, false)
            homePath = "${myFixture.testDataPath}/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PEST_FRAMEWORK_TYPE)
        PhpTestFrameworkSettingsManager.getInstance(project)
            .getOrCreateByInterpreter(PEST_FRAMEWORK_TYPE, interpreter, null, false)
            ?.executablePath = "randomPath"
        // Each run configuration clones the template. Create the template before the mocks, so that it keeps the real version detector.
        RunManager.getInstance(project).getConfigurationTemplate(PEST_RUN_CONFIGURATION_TYPE)
        mockDetector = mockk()
        mockkObject(PestVersionDetector.Companion)
        every { PestVersionDetector.instance } returns mockDetector
        every { mockDetector.getVersion(any(), any(), any()) } throws ExecutionException("not available in tests")
    }

    @AfterEach
    fun tearDown() {
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PEST_FRAMEWORK_TYPE, configurationsBackup)
        PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
        unmockkObject(PestVersionDetector.Companion)
        clearAllMocks()
        unmockkAll()
    }
}
