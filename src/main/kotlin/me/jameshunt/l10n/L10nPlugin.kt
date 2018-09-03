package me.jameshunt.l10n

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class L10nPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply L10n")

        val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"
        setupGeneratedSourceDirectory(generatedSrcPath)

        //todo: check hash of strings.xml to see if it changed, and if not do nothing
        val xmlData = StringsXmlParser(project.name).getXmlData()

        L10nGenerator(xmlData, generatedSrcPath, project.name).generateCode()

        val sourceSets = (project.extensions.getByName("android") as BaseExtension).sourceSets.asMap
        sourceSets.forEach { sourceSetName, sourceSet ->

            if (sourceSetName == "main") {
                println(sourceSet.java.srcDirs)
                val generatedSrcPathSrcSet = "./build/generated/source/L10n/src"
                println("maybe add: ${File(generatedSrcPathSrcSet).absolutePath}")
                sourceSet.java.srcDirs(File(generatedSrcPathSrcSet))

                println(sourceSet.java.srcDirs.toString())
            }
        }

    }

    private fun setupGeneratedSourceDirectory(generatedSrcPath: String) {

        val generatedSourceFolder = File(generatedSrcPath)

        println(generatedSourceFolder.absolutePath)

        if (generatedSourceFolder.exists()) return
        generatedSourceFolder.mkdirs()
    }
}