package com.pestphp.pest;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

    public static boolean isEnabled(@NotNull Project project) {
        return PhpTestFrameworkSettingsManager
            .getInstance(project)
            .getConfigurations(PestFrameworkType.getInstance())
            .stream()
            .anyMatch(config -> StringUtil.isNotEmpty(config.getExecutablePath()));
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
