package com.pestphp.pest;

import com.jetbrains.php.testFramework.PhpTestDescriptor;
import com.jetbrains.php.testFramework.PhpTestFrameworkConfigurationFactory;
import com.jetbrains.php.testFramework.PhpTestFrameworkFormDecorator;
import com.jetbrains.php.testFramework.PhpTestFrameworkFormDecorator.PhpDownloadableTestFormDecorator;
import com.jetbrains.php.testFramework.PhpTestFrameworkType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class PestFrameworkType extends PhpTestFrameworkType {
    @NonNls
    public static final String ID = "Pest";

    @NotNull
    public static PhpTestFrameworkType getInstance() {
        return PhpTestFrameworkType.getTestFrameworkType(ID);
    }

    @Override
    public @NotNull @Nls String getDisplayName() {
        return PestBundle.message("FRAMEWORK_NAME");
    }

    @Override
    public @NotNull String getID() {
        return ID;
    }

    @Override
    public @NotNull Icon getIcon() {
        return PestIcons.LOGO;
    }

    @Override
    public @Nullable PhpTestFrameworkFormDecorator getDecorator() {
        return new PhpDownloadableTestFormDecorator("https://github.com/pestphp/pest/releases");
    }
}
