package com.pestphp.pest.features.parallel

import com.intellij.execution.ExecutionException
import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.openapi.vfs.LocalFileSystem
import com.jetbrains.php.composer.ComposerDataService
import com.jetbrains.php.composer.configData.ComposerConfigManager
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import com.pestphp.pest.configuration.PestVersionDetector
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify

@TestDataPath("\$CONTENT_ROOT/../resources/com/pestphp/pest/features/parallel")
class PestParallelProgramRunnerTest : PestLightCodeFixture() {
    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var mockDetector: PestVersionDetector

    private data class MultiComposerPaths(
        val rootExecutablePath: String,
        val subprojectExecutablePath: String,
        val subprojectTestFilePath: String,
    )

    override fun getBasePath(): String = "${super.getBasePath()}/features/parallel"

    fun testCannotRunWrongExecutorId() = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertFalse(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID + "1", configuration))
    }

    fun testCanRunFile() = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    fun testCanRunFunction() = doTest {
        val testElement = myFixture.file?.firstChild?.lastChild?.firstChild ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    fun testCanRunDirectory() = doTest {
        val testElement = myFixture.file?.containingDirectory ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    fun testBuildFile() = doTest {
        val configuration = createConfiguration(myFixture.file)

        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src/ATest.php"
        assertEquals(expected, pestParallelCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildFunction() = doTest {
        val testElement = myFixture.file?.firstChild?.lastChild?.firstChild ?: return@doTest
        val configuration = createConfiguration(testElement)

        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src/ATest.php"
        assertEquals(
            expected,
            pestParallelCommandSettings.createGeneralCommandLine().parametersList.list
                .joinToString(" ")
                .substringBefore(" --filter")
        )
    }

    fun testBuildDirectory() = doTest {
        val testElement = myFixture.file?.containingDirectory ?: return@doTest
        val configuration = createConfiguration(testElement)
        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src"
        assertEquals(expected, pestParallelCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildWithParaTestModeWhenVersionAtLeast4_6_0() = doTest {
        every { mockDetector.getVersion(any(), any(), any()) } returns "4.6.0"
        val configuration = createConfiguration(myFixture.file)

        val expected = "randomPath --teamcity --parallel /src/ATest.php"
        assertEquals(expected, createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildWithLegacyModeWhenVersionBelow4_6_0() = doTest {
        every { mockDetector.getVersion(any(), any(), any()) } returns "4.5.0"
        val configuration = createConfiguration(myFixture.file)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src/ATest.php"
        assertEquals(expected, createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildWithParaTestModeUsesConfigurationFromBaseFileComposer() = doTest {
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
            createPestFrameworkConfiguration()
        }
        val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(psiElement))
        val runConfiguration = PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        runConfiguration!!.settings.commandLineSettings.interpreterSettings.interpreterName = getTestName(false)
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
            composerDataService.setConfigPathAndLibraryUpdateStatus(oldConfigPath, oldUpdateLibrary)
            composerConfigManager.setSubProjectConfigs(oldSubProjectConfigs)
        }
    }

    private fun createMultiComposerConfigurations(): MultiComposerPaths {
        val root = LocalFileSystem.getInstance().refreshAndFindFileByPath("$testDataPath/multiComposer")!!
        val interpreter = PhpInterpretersManagerImpl.getInstance(project).interpreters.single()
        val settingsManager = PhpTestFrameworkSettingsManager.getInstance(project)
        val rootExecutable = root.findFileByRelativePath("vendor/bin/pest")!!
        val subprojectExecutable = root.findFileByRelativePath("subproject/vendor/bin/pest")!!
        val subprojectTestFile = root.findFileByRelativePath("subproject/tests/ATest.php")!!
        val rootComposer = root.findFileByRelativePath("composer.json")!!
        val subprojectComposer = root.findFileByRelativePath("subproject/composer.json")!!

        ComposerDataService.getInstance(project).setConfigPathAndLibraryUpdateStatus(rootComposer.path, true)
        ComposerConfigManager.getInstance(project).addConfig(subprojectComposer)

        val rootConfiguration = settingsManager
            .getOrCreateByInterpreter(PestFrameworkType.instance, interpreter, rootComposer, false)
            ?.apply { executablePath = rootExecutable.path }
        val subprojectConfiguration = settingsManager
            .getOrCreateByInterpreter(PestFrameworkType.instance, interpreter, subprojectComposer, false)
            ?.apply { executablePath = subprojectExecutable.path }

        settingsManager.setConfigurations(
            PestFrameworkType.instance,
            listOfNotNull(rootConfiguration, subprojectConfiguration)
        )

        return MultiComposerPaths(rootExecutable.path, subprojectExecutable.path, subprojectTestFile.path)
    }

    private fun doTest(block: () -> Unit) {
        myFixture.configureByFile("ATest.php")
        block()
    }

    override fun setUp() {
        super.setUp()
        val interpreter = PhpInterpreter().apply {
            name = getTestName(false)
            homePath = "$testDataPath/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
        PhpTestFrameworkSettingsManager.getInstance(project)
            .getOrCreateByInterpreter(PestFrameworkType.instance, interpreter, null, false)
            ?.executablePath = "randomPath"
        mockDetector = mockk()
        mockkObject(PestVersionDetector.Companion)
        every { PestVersionDetector.instance } returns mockDetector
        every { mockDetector.getVersion(any(), any(), any()) } throws ExecutionException("not available in tests")
    }

    override fun tearDown() {
        try {
            PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, configurationsBackup)
            PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
            unmockkObject(PestVersionDetector.Companion)
        } catch (e: Throwable) {
            addSuppressedException(e)
        } finally {
            super.tearDown()
        }
    }
}