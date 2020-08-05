package com.pestphp.pest

import com.jetbrains.php.testFramework.PhpTestFrameworkFormDecorator
import com.jetbrains.php.testFramework.PhpTestFrameworkFormDecorator.PhpDownloadableTestFormDecorator
import com.jetbrains.php.testFramework.PhpTestFrameworkType
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class PestFrameworkType : PhpTestFrameworkType() {
    override fun getDisplayName(): @Nls String {
        return PestBundle.message("FRAMEWORK_NAME")
    }

    override fun getID(): String {
        return ID
    }

    override fun getIcon(): Icon {
        return PestIcons.LOGO
    }

    override fun getDecorator(): PhpTestFrameworkFormDecorator? {
        return PhpDownloadableTestFormDecorator("https://github.com/pestphp/pest/releases")
    }

    companion object {
        @NonNls
        val ID = "Pest"
        val instance: PhpTestFrameworkType
            get() = getTestFrameworkType(ID)
    }
}
