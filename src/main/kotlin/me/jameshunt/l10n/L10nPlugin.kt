package me.jameshunt.l10n

import com.android.build.gradle.BaseExtension
import me.jameshunt.l10n.generate.L10nImplementation
import me.jameshunt.l10n.generate.LanguageImplementation
import me.jameshunt.l10n.generate.LanguageInterface
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class L10nPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            println("apply L10n")

            val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"

            setupGeneratedSourceDirectory(generatedSrcPath)

            generateCode(project.name, generatedSrcPath)

            addSourceSet(project)
        }
    }

    private fun setupGeneratedSourceDirectory(generatedSrcPath: String) {
        val generatedSourceFolder = File(generatedSrcPath)

        if (generatedSourceFolder.exists()) return
        generatedSourceFolder.mkdirs()
    }

    private fun generateCode(projectName: String, generatedSrcPath: String) {
        val packageName = "me.jameshunt.$projectName"
        val xmlData = AndroidStringsParser(projectName).getXmlData()

        LanguageInterface(generatedSrcPath, packageName).generate(xmlData)
        LanguageImplementation(generatedSrcPath, packageName).generate(xmlData)
        L10nImplementation(generatedSrcPath, packageName).generate(xmlData)
    }

    private fun addSourceSet(project: Project) {
        val sourceSets = (project.extensions.getByName("android") as BaseExtension).sourceSets.asMap
        sourceSets.forEach { sourceSetName, sourceSet ->

            if (sourceSetName == "main") {
                val generatedSrcPathSrcSet = "./build/generated/source/L10n/src"
                sourceSet.java.srcDirs(File(generatedSrcPathSrcSet))
                return@forEach
            }
        }
    }
}