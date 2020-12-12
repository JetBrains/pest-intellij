package com.pestphp.pest

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo
import com.intellij.openapi.util.IconLoader

object PestIcons {
    val LOGO = IconLoader.getIcon("/logo.svg", PestIcons.javaClass)
    val RUN_SINGLE_TEST = IconLoader.getIcon("/run@${getThemeString()}.svg", PestIcons.javaClass)
    val CONFIG = IconLoader.getIcon("/config@${getThemeString()}.svg", PestIcons.javaClass)
    val FILE = IconLoader.getIcon("/file@${getThemeString()}.svg", PestIcons.javaClass)

    private fun isDarkMode() = LafManager.getInstance().currentLookAndFeel == DarculaLookAndFeelInfo()
    private fun getThemeString(): String {
        if (isDarkMode()) {
            return "dark"
        }
        return "light"
    }
}
