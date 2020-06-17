package com.pestphp.pest;

import com.jetbrains.php.testFramework.PhpTestFrameworkType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PestFrameworkType extends PhpTestFrameworkType {
    @Override
    public @NotNull @Nls String getDisplayName() {
        return PestBundle.message("FRAMEWORK_NAME");
    }

    @Override
    public @NotNull String getID() {
        return "Pest";
    }

    @Override
    public @NotNull Icon getIcon() {
        return PestIcons.LOGO;
    }
}
