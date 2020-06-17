package com.pestphp.pest;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.pestphp.pest.configuration.PestRunConfiguration;
import com.pestphp.pest.configuration.PestRunConfigurationType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PestUtil {
    private static final String NOTIFICATION_GROUP = "Pest";

    public static boolean isPestTestFile(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        if (!(element instanceof PhpFile)) {
            return false;
        }

        Collection<FunctionReference> functions = PsiTreeUtil.findChildrenOfType(element, FunctionReference.class);

        return functions.stream()
            .anyMatch(PestUtil::isPestTestFunction);
    }

    public static boolean isPestTestFunction(@NotNull FunctionReference reference) {
        String s = reference.getCanonicalText();
        return s.equals("it") || s.equals("test");
    }

    public static @Nullable String getTestName(FunctionReference element) {
        PsiElement parameter = element.getParameter(0);

        if (!(parameter instanceof StringLiteralExpression)) {
            return null;
        }

        return ((StringLiteralExpression) parameter).getContents();
    }

    public static List<PestRunConfiguration> getRunConfigurations(@NotNull Project project) {
        return RunManager.getInstance(project)
            .getConfigurationSettingsList(PestRunConfigurationType.class)
            .stream()
            .map(RunnerAndConfigurationSettings::getConfiguration)
            .filter(configuration -> configuration instanceof PestRunConfiguration)
            .map(configuration -> (PestRunConfiguration) configuration)
            .collect(Collectors.toList());
    }

    @Nullable
    public static PestRunConfiguration getMainConfiguration(
        @NotNull Project project,
        @NotNull List<PestRunConfiguration> configurations
    ) {
        @Nullable PestRunConfiguration mainConfiguration = configurations.stream()
            .filter(configuration -> "tests".equals(configuration.getName()))
            .findAny()
            .orElse(null);
        if (mainConfiguration == null) {
            PestUtil.doNotify(
                PestBundle.message("runConfiguration.mainConfiguration.missing.title"),
                PestBundle.message("runConfiguration.mainConfiguration.missing.description"),
                NotificationType.ERROR,
                project
            );
            return null;

        } else {
            try {
                mainConfiguration.checkConfiguration();
                return mainConfiguration;

            } catch (RuntimeConfigurationException ex) {
                PestUtil.doNotify(
                    PestBundle.message("runConfiguration.mainConfiguration.invalid.title"),
                    PestBundle.message("runConfiguration.mainConfiguration.invalid.description"),
                    NotificationType.ERROR,
                    project
                );
            }
            return null;
        }
    }

    public static void doNotify(
        @NotNull String title,
        @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String content,
        @NotNull NotificationType type,
        @Nullable Project project
    ) {
        Notification notification = new Notification(NOTIFICATION_GROUP, title, content, type);
        doNotify(notification, project);
    }

    public static void doNotify(Notification notification, @Nullable Project project) {
        if (project != null && !project.isDisposed() && !project.isDefault()) {
            project.getMessageBus().syncPublisher(Notifications.TOPIC).notify(notification);
        } else {
            Application app = ApplicationManager.getApplication();
            if (!app.isDisposed()) {
                app.getMessageBus().syncPublisher(Notifications.TOPIC).notify(notification);
            }
        }
    }
}
