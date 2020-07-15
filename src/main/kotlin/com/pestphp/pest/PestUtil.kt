package com.pestphp.pest

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ObjectUtils
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import org.jetbrains.annotations.Nls

object PestUtil {
    private const val NOTIFICATION_GROUP = "Pest"

    @JvmStatic
    fun isPestTestFile(element: PsiElement?): Boolean {
        if (element == null) {
            return false
        }
        if (element !is PhpFile) {
            return false
        }
        val functions = PsiTreeUtil.findChildrenOfType(element, FunctionReference::class.java)
        return functions.stream()
                .anyMatch { obj: FunctionReference? -> isPestTestFunction(obj) }
    }

    @JvmStatic
    fun isPestTestFunction(element: PsiElement?): Boolean {
        return when (element) {
            null -> false
            is MethodReference -> isPestTestFunction(element)
            is FunctionReference -> isPestTestFunction(element)
            else -> false
        }
    }

    @JvmStatic
    fun isPestTestFunction(reference: FunctionReference): Boolean {
        return reference.canonicalText in setOf("it", "test")
    }

    @JvmStatic
    fun isPestTestFunction(methodReference: MethodReference): Boolean {
        val reference = ObjectUtils.tryCast(methodReference.classReference, FunctionReference::class.java)
        return reference != null && isPestTestFunction(reference)
    }

    @JvmStatic
    fun getTestName(element: FunctionReference): String? {
        return when (val parameter = element.getParameter(0)) {
            is StringLiteralExpression -> parameter.contents
            else -> null
        }
    }

    @JvmStatic
    fun getTestName(element: PsiElement?): String? {
        return when (element) {
            is MethodReference -> (element.classReference as? FunctionReference)?.let(this::getTestName)
            is FunctionReference -> getTestName(element)
            else -> null
        }
    }

    @JvmStatic
    fun isEnabled(project: Project): Boolean {
        return PhpTestFrameworkSettingsManager
                .getInstance(project)
                .getConfigurations(PestFrameworkType.getInstance())
                .stream()
                .anyMatch { config: PhpTestFrameworkConfiguration -> StringUtil.isNotEmpty(config.executablePath) }
    }

    @JvmStatic
    fun doNotify(
            title: String,
            content: @Nls(capitalization = Nls.Capitalization.Sentence) String,
            type: NotificationType,
            project: Project?
    ) {
        val notification = Notification(NOTIFICATION_GROUP, title, content, type)
        doNotify(notification, project)
    }

    @JvmStatic
    fun doNotify(notification: Notification?, project: Project?) {
        if (project != null && !project.isDisposed && !project.isDefault) {
            project.messageBus.syncPublisher(Notifications.TOPIC).notify(notification!!)
        } else {
            val app = ApplicationManager.getApplication()
            if (!app.isDisposed) {
                app.messageBus.syncPublisher(Notifications.TOPIC).notify(notification!!)
            }
        }
    }
}