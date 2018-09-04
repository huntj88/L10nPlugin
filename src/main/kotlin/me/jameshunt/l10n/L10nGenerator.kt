package me.jameshunt.l10n

import java.io.BufferedWriter
import java.io.File

class L10nGenerator(
        private val xmlData: List<LanguageSet>,
        private val generatedSrcPath: String,
        projectName: String
) {
    private val packageName: String = "me.jameshunt.$projectName"

    fun generateCode() {
        //xmlData.first().variables.keys.toList().generateLanguageInterface()
        xmlData.first().generateLanguageInterface()

        xmlData.generateLanguageImplementations()

        xmlData.generateL10n()
    }

    private fun LanguageSet.generateLanguageInterface() {
        File("$generatedSrcPath/Language.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("interface Language {")

            val pattern = Regex("%[0-9]\\$[sd]")

            this.variables.forEach { key, value ->
                val placeHolders = pattern.findAll(value).map { it.value }.toList()

                println("size: ${placeHolders.size}")

                when (placeHolders.isEmpty()) {
                    true -> out.writeLn("    val $key: String")
                    false -> {

                        val parameters = placeHolders.foldIndexed("(") { index, acc, next ->
                            "${acc}param$index: String, "
                        }.substringBeforeLast(",") + ")"

                        out.writeLn("    fun $key$parameters: String")
                    }
                }
            }

            out.writeLn("}")
        }
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

                languageSet.variables.forEach {
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
                out.writeLn("        get() = selectedLanguage.$it")
                out.newLine()
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

    private fun BufferedWriter.writeLn(line: String) {
        this.write(line)
        this.newLine()
    }
}