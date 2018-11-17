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

        //val generateTask = "generateL10n"

//        project.tasks.create(generateTask) {
//
//            val action = Action<Task> {
//                println("apply L10n")
//
//                val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"
//
//                setupGeneratedSourceDirectory(generatedSrcPath)
//
//                generateCode(project.name, generatedSrcPath)
//
//                addSourceSet(project)
//            }
//
//            it.actions.add(action)
//        }

        project.afterEvaluate {

            project
                    .gradle
                    .taskGraph
                    .allTasks
                    .also { it.forEach { task -> println(task.name) } }
                    .firstOrNull { task -> task.name.contains("clean") }
                    ?.let { firstTask ->
                        firstTask.doFirst {
                            println("apply L10n")

                            val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"

                            setupGeneratedSourceDirectory(generatedSrcPath)

                            generateCode(project.name, generatedSrcPath)

                            addSourceSet(project)
                        }
                    }

//            project.tasks.firstOrNull()?.let { firstTask ->
//                firstTask.doFirst {
//                    println("apply L10n")
//
//                    val generatedSrcPath = "./${project.name}/build/generated/source/L10n/src"
//
//                    setupGeneratedSourceDirectory(generatedSrcPath)
//
//                    generateCode(project.name, generatedSrcPath)
//
//                    addSourceSet(project)
//                }
//            }

//            project.tasks.getByName("preBuild").finalizedBy(
//                project.tasks.getByName(generateTask)
//            )

//            project.tasks.forEach { task ->
//                println(task.name)
//            }
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