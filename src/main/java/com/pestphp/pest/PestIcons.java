package com.pestphp.pest;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import com.jetbrains.php.PhpIcons;

import javax.swing.*;

public class PestIcons {
    public static final Icon LOGO = IconLoader.getIcon("/logo.svg");
    public static final Icon RUN_SINGLE_TEST = new LayeredIcon(LOGO, AllIcons.RunConfigurations.TestState.Run);
    public static final Icon CONFIG = IconLoader.getIcon("/logo.svg");
}
