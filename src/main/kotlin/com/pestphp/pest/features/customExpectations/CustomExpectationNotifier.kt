package com.pestphp.pest.features.customExpectations

import com.intellij.psi.PsiFile
import com.intellij.util.messages.Topic
import com.pestphp.pest.features.customExpectations.generators.Method
import java.util.EventListener

interface CustomExpectationNotifier : EventListener {
    companion object {
        @Topic.ProjectLevel
        val TOPIC = Topic.create("Custom expectation", CustomExpectationNotifier::class.java)
    }

    fun changedExpectation(file: PsiFile, customExpectations: List<Method>)
}