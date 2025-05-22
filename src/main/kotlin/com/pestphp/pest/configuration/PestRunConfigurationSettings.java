package com.pestphp.pest.configuration;

import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings;
import org.jetbrains.annotations.NotNull;

public class PestRunConfigurationSettings extends PhpTestRunConfigurationSettings {
  @Override
  protected @NotNull PestRunnerSettings createDefault() {
    return new PestRunnerSettings();
  }

  @Property(surroundWithTag = false)
  public @NotNull PestRunnerSettings getPestRunnerSettings() {
    final PhpTestRunnerSettings settings = super.getRunnerSettings();
    if (settings instanceof PestRunnerSettings) {
      return (PestRunnerSettings)settings;
    }

    final PestRunnerSettings copy = PestRunnerSettings.fromPhpTestRunnerSettings(settings);
    setPestRunnerSettings(copy);
    return copy;
  }

  public void setPestRunnerSettings(PestRunnerSettings runnerSettings) {
    setRunnerSettings(runnerSettings);
  }

  /**
   * @deprecated Use {@link #getPestRunnerSettings()}
   **/
  @Deprecated
  @Transient
  @Override
  public @NotNull PhpTestRunnerSettings getRunnerSettings() {
    return super.getRunnerSettings();
  }
}
