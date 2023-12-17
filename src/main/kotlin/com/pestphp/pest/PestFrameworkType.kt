package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.jetbrains.php.testFramework.PhpTestFrameworkFormDecorator
import com.jetbrains.php.testFramework.PhpTestFrameworkFormDecorator.PhpDownloadableTestFormDecorator
import com.jetbrains.php.testFramework.PhpTestFrameworkType
import com.jetbrains.php.testFramework.ui.PhpTestFrameworkBaseConfigurableForm
import com.jetbrains.php.testFramework.ui.PhpTestFrameworkBySdkConfigurableForm
import com.jetbrains.php.testFramework.ui.PhpTestFrameworkConfigurableForm
import com.pestphp.pest.configuration.PestVersionDetector
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

/**
 * Registers a framework type for PHP.
 *
 * This class is used to show the menu in
 * `Preferences -> Languages & Frameworks -> PHP -> Test Frameworks`
 */
class PestFrameworkType : PhpTestFrameworkType() {
    private val pestUrl = "https://github.com/pestphp/pest/releases"

    override fun getDisplayName(): @Nls String {
        return PestBundle.message("FRAMEWORK_NAME")
    }

    override fun getID(): String {
        return ID
    }

    override fun getIcon(): Icon {
        return PestIcons.Logo
    }

    override fun getDecorator(): PhpTestFrameworkFormDecorator {
        return object : PhpDownloadableTestFormDecorator(pestUrl) {
            override fun decorate(
                project: Project,
                form: PhpTestFrameworkBaseConfigurableForm<*>
            ): PhpTestFrameworkConfigurableForm<*> {
                if (form !is PhpTestFrameworkBySdkConfigurableForm) {
                    form.setVersionDetector(PestVersionDetector.instance)
                }
                return super.decorate(project, form)
            }
        }
    }

    companion object {
        @NonNls
        val ID = "Pest"
        val instance: PhpTestFrameworkType
            get() = getTestFrameworkType(ID)
    }
}
