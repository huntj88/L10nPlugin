package me.jameshunt.l10n.generate

import me.jameshunt.l10n.LanguageSet
import java.io.BufferedWriter
import java.io.File

class LanguageInterface(
        private val generatedSrcPath: String,
        private val packageName: String
) {

    fun generate(xmlData: List<LanguageSet>) {
        xmlData.first().generateLanguageInterface()
    }

    private fun LanguageSet.generateLanguageInterface() {
        File("$generatedSrcPath/Language.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("interface Language {")

            this.variables.forEach { key, value ->
                val placeHolders = placeholderPattern.findAll(value).map { it.value }.toList()

                when (placeHolders.isEmpty()) {
                    true -> out.writeLn("    val $key: String")
                    false -> out.generatePlaceholderFunction(key, placeHolders)
                }
            }

            out.writeLn("}")
        }
    }

    private fun BufferedWriter.generatePlaceholderFunction(key: String, placeHolders: List<String>) {
        val parameters = placeHolders
                .foldIndexed("(") { index, acc, _ -> "${acc}param$index: String, " }
                .substringBeforeLast(",") + ")"

        this.writeLn("    fun $key$parameters: String")
    }
}