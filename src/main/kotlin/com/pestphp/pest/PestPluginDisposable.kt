package com.pestphp.pest

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

/**
 * The service is intended to be used instead of a project/application as a parent disposable.
 */
@Service(Service.Level.APP, Service.Level.PROJECT)
class PestPluginDisposable : Disposable {
    override fun dispose() {}

    companion object {
        @JvmStatic
        fun getInstance(): Disposable = ApplicationManager.getApplication().getService(PestPluginDisposable::class.java)

        @JvmStatic
        fun getInstance(project: Project): Disposable = project.getService(PestPluginDisposable::class.java)
    }
}
