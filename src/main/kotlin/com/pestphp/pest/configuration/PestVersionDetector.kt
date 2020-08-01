package com.pestphp.pest.configuration;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.php.PhpTestFrameworkVersionDetector;
import com.pestphp.pest.PestBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class PestVersionDetector extends PhpTestFrameworkVersionDetector<String> {
    private static final Logger LOG = Logger.getInstance(PestVersionDetector.class);
    private static PestVersionDetector myInstance;


    public static PestVersionDetector getInstance() {
        if (myInstance == null) {
            myInstance = new PestVersionDetector();
        }

        return myInstance;
    }

    @Override
    protected @NotNull @Nls String getPresentableName() {
        return PestBundle.message("FRAMEWORK_NAME");
    }

    @NotNull
    @Override
    protected String parse(@NotNull String s) {
        LOG.info(String.format("Parsing version: %s", s));
        return "0.2";
    }
}
