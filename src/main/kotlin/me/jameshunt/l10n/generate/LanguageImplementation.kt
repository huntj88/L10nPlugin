package me.jameshunt.l10n.generate

import me.jameshunt.l10n.LanguageSet
import java.io.BufferedWriter
import java.io.File

class LanguageImplementation(
        private val generatedSrcPath: String,
        private val packageName: String
) {

    fun generate(xmlData: List<LanguageSet>) {
        xmlData.generateLanguageImplementations()
    }

    private fun List<LanguageSet>.generateLanguageImplementations() {

        this.forEach { languageSet ->
            val fileName = when (languageSet.language) {
                "default" -> "Default"
                else -> languageSet.language.toUpperCase()
            }

            val path = "$generatedSrcPath/$fileName.kt"

            File(path).bufferedWriter().use { out ->
                out.writeLn("package $packageName")
                out.newLine()

                when (languageSet.language == "default") {
                    true -> out.writeLn("class $fileName: Language {")
                    false -> out.writeLn("class $fileName: Language by Default() {")
                }

                languageSet.variables.forEach { key, value ->
                    val placeHolders = placeholderPattern.findAll(value).map { it.value }.toList()

                    when (placeHolders.isEmpty()) {
                        true -> out.writeLn("    override val $key = \"$value\"")
                        false -> out.generatePlaceholderFunction(key, value, placeHolders)
                    }
                }

                out.writeLn("}")
            }
        }
    }

    private fun BufferedWriter.generatePlaceholderFunction(key: String, value: String, placeHolders: List<String>) {
        val parameters = placeHolders
                .foldIndexed("(") { index, acc, _ -> "${acc}param$index: String, " }
                .substringBeforeLast(",") + ")"

        val afterEquals = placeHolders
                .foldIndexed("\"$value\"") { index, acc, string ->
                    acc.replace(string, "\$param$index")
                }

        this.writeLn("    override fun $key$parameters: String = $afterEquals")
    }
}