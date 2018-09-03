package me.jameshunt.l10n

import java.io.BufferedWriter
import java.io.File

class L10nGenerator(
        private val xmlData: List<LanguageSet>,
        private val generatedSrcPath: String
) {
    private val packageName: String = "me.jameshunt"

    fun generateCode() {
        xmlData.first().variables.keys.toList().generateLanguageInterface()

        xmlData.generateLanguageImplementations()

        xmlData.generateL10n()
    }

    private fun List<String>.generateLanguageInterface() {
        File("$generatedSrcPath/Language.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("interface Language {")

            this.forEach {
                out.writeLn("    val $it: String")
            }

            out.writeLn("}")
        }
    }

    private fun List<LanguageSet>.generateLanguageImplementations() {

        this.forEach {
            val fileName = when (it.language) {
                "default" -> "Default"
                else -> it.language.toUpperCase()
            }

            val path = "$generatedSrcPath/$fileName.kt"

            File(path).bufferedWriter().use { out ->
                out.writeLn("package $packageName")
                out.newLine()

                out.writeLn("class $fileName: Language {")

                it.variables.forEach {
                    out.writeLn("    override val ${it.key} = \"${it.value}\"")
                }

                out.writeLn("}")
            }
        }
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

            default.variables.keys.forEach {
                out.writeLn("    val $it: String")
                out.writeLn("        get() = androidStrings.$it")
                out.newLine()
            }

            out.writeLn("    private val androidStrings: Language by lazy {")
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

    private fun BufferedWriter.writeLn(line: String) {
        this.write(line)
        this.newLine()
    }
}