package com.pestphp.pest.configuration

import com.intellij.openapi.editor.ReadOnlyModificationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.UI
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine.CoverageEngine
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationEditor
import java.awt.BorderLayout
import java.lang.reflect.InvocationTargetException
import javax.swing.JComponent
import javax.swing.JPanel

class PestTestRunConfigurationEditor(
    private val parentEditor: PhpTestRunConfigurationEditor,
    settings: PestRunConfiguration
) : SettingsEditor<PestRunConfiguration>() {
    private val myMainPanel = JPanel()
    private var coveragePanel = JPanel()
    private val coverageEngineComboBox = ComboBox(arrayOf(CoverageEngine.XDEBUG, CoverageEngine.PCOV))

    init {
        coveragePanel = UI.PanelFactory.grid().add(
            UI.PanelFactory.panel(coverageEngineComboBox).withLabel("Preferred Coverage engine: ")
        ).createPanel()

        myMainPanel.layout = BorderLayout()
        myMainPanel.add(parentEditor.component, BorderLayout.CENTER)
        myMainPanel.add(coveragePanel, BorderLayout.SOUTH)
        resetEditorFrom(settings)
    }

    override fun createEditor(): JComponent {
        return myMainPanel
    }

    private fun doApply(configuration: PestRunConfiguration) {
        val settings = configuration.settings as PestRunConfigurationSettings
        val runnerSettings = settings.runnerSettings

        runnerSettings.coverageEngine = coverageEngineComboBox.selectedItem as CoverageEngine
    }

    private fun doReset(configuration: PestRunConfiguration) {
        val settings = configuration.settings as PestRunConfigurationSettings
        val runnerSettings = settings.runnerSettings

        coverageEngineComboBox.selectedItem = runnerSettings.coverageEngine
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
