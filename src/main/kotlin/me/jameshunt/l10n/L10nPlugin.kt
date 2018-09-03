package me.jameshunt.l10n

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class L10nPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("apply L10n")

        val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"
        setupGeneratedSourceDirectory(generatedSrcPath)

        //todo: check hash of strings.xml to see if it changed, and if not do nothing
        val xmlData = StringsXmlParser(project.name).getXmlData()
        L10nGenerator(xmlData, generatedSrcPath).generateCode()
    }

    private fun setupGeneratedSourceDirectory(generatedSrcPath: String) {

        val wallpaperDirectory = File(generatedSrcPath)
        if (wallpaperDirectory.exists()) return
        wallpaperDirectory.mkdirs()
    }
}