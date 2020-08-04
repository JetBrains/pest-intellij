package com.pestphp.pest

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.pestphp.pest.parser.PestConfigurationFile
import com.pestphp.pest.parser.PestConfigurationFileParser

@State(name = "PestSettings", storages = [Storage("pest.xml")])
class PestSettings: PersistentStateComponent<PestSettings> {
    var pestFilePath = "tests/Pest.php"

    override fun getState(): PestSettings? {
        return this
    }

    override fun loadState(state: PestSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    private val parser = PestConfigurationFileParser(this)

    fun getPestConfiguration(project: Project): PestConfigurationFile {
        return parser.parse(project)
    }

    companion object {
        fun getInstance(project: Project): PestSettings {
            return ServiceManager.getService(project, PestSettings::class.java)
        }
    }
}