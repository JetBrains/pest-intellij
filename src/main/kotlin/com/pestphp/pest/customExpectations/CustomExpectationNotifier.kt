package com.pestphp.pest.customExpectations

import com.intellij.psi.PsiFile
import com.intellij.util.messages.Topic
import com.pestphp.pest.customExpectations.generators.Method
import java.util.*

interface CustomExpectationNotifier : EventListener {
    companion object {
        @Topic.ProjectLevel
        val TOPIC = Topic.create("Custom expectation", CustomExpectationNotifier::class.java)
    }

    fun changedExpectation(file: PsiFile, customExpectations: List<Method>)
}