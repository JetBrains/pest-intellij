package com.pestphp.pest.configuration

import com.intellij.openapi.editor.ReadOnlyModificationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine.CoverageEngine
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationEditor
import com.pestphp.pest.PestBundle
import java.lang.reflect.InvocationTargetException
import javax.swing.JComponent

class PestTestRunConfigurationEditor(
    private val parentEditor: PhpTestRunConfigurationEditor,
    settings: PestRunConfiguration
) : SettingsEditor<PestRunConfiguration>() {
    private val myMainPanel: DialogPanel
    private lateinit var coverageEngineComboBox: ComboBox<CoverageEngine>
    private lateinit var enabledParallelTestingCheckBox: JBCheckBox

    init {
        myMainPanel = panel {
            row {
                cell(parentEditor.component)
                    .align(AlignX.FILL)
            }
            row(PestBundle.message("COVERAGE_ENGINE_LABEL_TEXT")) {
                coverageEngineComboBox = comboBox(listOf(CoverageEngine.XDEBUG, CoverageEngine.PCOV))
                    .align(AlignX.FILL)
                    .component
            }
            row(PestBundle.message("ENABLE_PARALLEL_TESTING_LABEL_TEXT")) {
                enabledParallelTestingCheckBox = checkBox("")
                    .component
            }.layout(RowLayout.INDEPENDENT)
        }
        resetEditorFrom(settings)
    }

    override fun createEditor(): JComponent {
        return myMainPanel
    }

    private fun doApply(configuration: PestRunConfiguration) {
        val settings = configuration.settings as PestRunConfigurationSettings
        val runnerSettings = settings.pestRunnerSettings

        runnerSettings.coverageEngine = coverageEngineComboBox.selectedItem as CoverageEngine
        runnerSettings.parallelTestingEnabled = enabledParallelTestingCheckBox.isSelected
    }

    private fun doReset(configuration: PestRunConfiguration) {
        val settings = configuration.settings as PestRunConfigurationSettings
        val runnerSettings = settings.pestRunnerSettings

        coverageEngineComboBox.selectedItem = runnerSettings.coverageEngine
        enabledParallelTestingCheckBox.isSelected = runnerSettings.parallelTestingEnabled
    }

    override fun resetEditorFrom(settings: PestRunConfiguration) {
        doReset(settings)
        parentEditor.javaClass.declaredMethods.find { it.name == "resetEditorFrom" }!!.let {
            it.isAccessible = true
            it.invoke(parentEditor, settings)
        }
    }

    override fun applyEditorTo(settings: PestRunConfiguration) {
        parentEditor.javaClass.declaredMethods.find { it.name == "applyEditorTo" }!!.let {
            it.isAccessible = true
            try {
                it.invoke(parentEditor, settings)
            } catch (exception: InvocationTargetException) {
                // In case the method throws a read only error (happens in code with me) we ignore it.
                if (exception.cause is ReadOnlyModificationException) {
                    return@let
                }

                throw exception
            }
        }
        doApply(settings)
    }

    override fun getSnapshot(): PestRunConfiguration {
        val result = parentEditor.snapshot as PestRunConfiguration
        doApply(result)
        return result
    }
}
