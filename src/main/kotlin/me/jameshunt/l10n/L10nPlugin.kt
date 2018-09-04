package me.jameshunt.l10n

import com.android.build.gradle.BaseExtension
import me.jameshunt.l10n.generate.L10nImplementation
import me.jameshunt.l10n.generate.LanguageImplementation
import me.jameshunt.l10n.generate.LanguageInterface
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class L10nPlugin : Plugin<Project> {

    /** TODO
     *  hash of each string file. feed the ones that changed to the generator
     *
     *  lol, what if i made a generated git repo (local only) inside the build/generated/source/L10n/src folder?
     *
     *  then i could use it to track changes to the strings.xml files(using symlink or something), and only generate a little bit of code.
     *  could maybe get incremental changes to the language file
     *
     *  if there were version conflicts just force push
     */

    override fun apply(project: Project) {
        println("apply L10n")
        val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"

        setupGeneratedSourceDirectory(generatedSrcPath)

        generateCode(project.name, generatedSrcPath)

        addSourceSet(project)
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