package com.pestphp.pest

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.XmlSerializerUtil
import com.pestphp.pest.parser.PestConfigurationFile
import com.pestphp.pest.parser.PestConfigurationFileParser

@Service(Service.Level.PROJECT)
@State(name = "PestSettings", storages = [Storage("pest.xml")])
class PestSettings : PersistentStateComponent<PestSettings> {
    var pestFilePath = "tests/Pest.php"
    var preferredTestFlavor =  TestFlavor.IT

    enum class TestFlavor {
        IT,
        TEST
    }

    override fun getState(): PestSettings {
        return this
    }

    override fun loadState(state: PestSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    private val parser = PestConfigurationFileParser(this)

    fun getPestConfiguration(project: Project, virtualFile: VirtualFile? = null): PestConfigurationFile {
        return parser.parse(project, virtualFile)
    }

    companion object {
        fun getInstance(project: Project): PestSettings {
            return project.service()
        }
    }
}
