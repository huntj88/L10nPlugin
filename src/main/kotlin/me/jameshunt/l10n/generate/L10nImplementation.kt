package me.jameshunt.l10n.generate

import me.jameshunt.l10n.LanguageSet
import java.io.BufferedWriter
import java.io.File

class L10nImplementation(
        private val generatedSrcPath: String,
        private val packageName: String
) {

    fun generate(xmlData: List<LanguageSet>) {
        xmlData.generateL10n()
    }
    private fun List<LanguageSet>.generateL10n() {

        val defaultLastSorted = this.sortedBy { it.language == "default" }

        val default = defaultLastSorted.last()
        val otherLanguages = when (defaultLastSorted.isNotEmpty()) {
            true -> defaultLastSorted.dropLast(1)
            false -> emptyList()
        }

        File("$generatedSrcPath/L10n.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("import java.util.Locale")
            out.newLine()

            out.writeLn("object L10n {")

            default.variables.forEach { key, value ->
                val placeHolders = placeholderPattern.findAll(value).map { it.value }.toList()

                when (placeHolders.isEmpty()) {
                    true -> out.generateStringProperty(key)
                    false -> out.generatePlaceholderFunction(key, placeHolders)
                }
            }

            out.writeLn("    private val selectedLanguage: Language by lazy {")
            out.writeLn("        when (Locale.getDefault().language) {")

            otherLanguages.forEach {
                out.writeLn("            \"${it.language}\" -> ${it.language.toUpperCase()}()")
            }

            out.writeLn("            else -> Default()")
            out.writeLn("        }")
            out.writeLn("    }")
            out.writeLn("}")
        }
    }

    private fun BufferedWriter.generateStringProperty(key: String) {
        this.writeLn("    val $key: String")
        this.writeLn("        get() = selectedLanguage.$key")
        this.newLine()
    }

    private fun BufferedWriter.generatePlaceholderFunction(key: String, placeHolders: List<String>) {
        val parameters = placeHolders
                .foldIndexed("(") { index, acc, _ -> "${acc}param$index: String, " }
                .substringBeforeLast(",") + ")"

        val parametersEnd = placeHolders
                .foldIndexed("(") { index, acc, _ -> "${acc}param$index, " }
                .substringBeforeLast(",") + ")"

        this.writeLn("    fun $key$parameters: String = selectedLanguage.$key$parametersEnd")
        this.newLine()
    }
}