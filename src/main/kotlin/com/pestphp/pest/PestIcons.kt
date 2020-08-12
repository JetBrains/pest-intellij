package com.pestphp.pest

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo
import com.intellij.openapi.util.IconLoader

object PestIcons {
    val LOGO = IconLoader.getIcon("/logo.svg")
    val RUN_SINGLE_TEST = IconLoader.getIcon("/run@${getThemeString()}.svg")
    val CONFIG = IconLoader.getIcon("/config@${getThemeString()}.svg")
    val FILE = IconLoader.getIcon("/file@${getThemeString()}.svg")

    private fun isDarkMode() = LafManager.getInstance().currentLookAndFeel == DarculaLookAndFeelInfo()
    private fun getThemeString(): String {
        if (isDarkMode()) {
            return "dark"
        }
        return "light"
    }
}
