package com.pestphp.pest.action;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.psi.PsiElement;
import com.pestphp.pest.PestBundle;
import com.pestphp.pest.PestIcons;
import com.pestphp.pest.PestUtil;
import com.pestphp.pest.configuration.PestRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CreateRunSingleTestAction extends BasePestRunAction {
    private final String path;
    private final String testName;

    public CreateRunSingleTestAction(@NotNull PsiElement element,
                                     @NotNull String testName) {
        super(element.getProject());

        this.path = element.getContainingFile().getVirtualFile().getPath();
        this.testName = testName;

        getTemplatePresentation().setText(PestBundle.message(
            "CREATE_SINGLE_TEST",
            testName
        ));
        getTemplatePresentation().setIcon(PestIcons.CONFIG);
    }

    @Nullable
    protected RunnerAndConfigurationSettings prepareAction() {
        wasCreated = false;
        List<PestRunConfiguration> configurations = PestUtil.getRunConfigurations(getProject());
        List<PestRunConfiguration> samePath = configurations.stream()
            .filter(configuration -> {
                try {
                    configuration.checkConfiguration();
                } catch (RuntimeConfigurationException ex) {
                    return false;
                }
                return path.equals(configuration.getSettings().getTestScope());
            })
            .collect(Collectors.toList());

        RunManager manager = RunManager.getInstance(getProject());
        if (samePath.size() == 0) {
            PestRunConfiguration mainConfiguration = PestUtil.getMainConfiguration(getProject(), configurations);
            if (mainConfiguration == null || mainConfiguration.getFactory() == null) {
                return null;
            }

            PestRunConfiguration current = (PestRunConfiguration) mainConfiguration.clone();
            current.setName("tests:" + testName);
            current.getSettings().setTestScope(path);
            RunnerAndConfigurationSettings action = manager.createConfiguration(current, mainConfiguration.getFactory());
            action.setTemporary(isTemporary());

            manager.addConfiguration(action);
            manager.setSelectedConfiguration(action);
            wasCreated = true;
            return action;

        } else {
            PestRunConfiguration existing = samePath.get(0);
            if (existing.getFactory() == null) {
                return null;
            }

            RunnerAndConfigurationSettings action = manager.createConfiguration(existing, existing.getFactory());
            manager.setSelectedConfiguration(action);
            return action;
        }
    }
}
