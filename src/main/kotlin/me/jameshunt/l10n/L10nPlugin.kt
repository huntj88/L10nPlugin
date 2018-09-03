package me.jameshunt.l10n

import org.gradle.api.Plugin
import org.gradle.api.Project

class L10nPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        println("apply L10n")
        GenerationTask(project.name).generateLocalizationCode()
    }
}