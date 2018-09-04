package me.jameshunt.l10n

import java.io.BufferedWriter
import java.io.File

class L10nGenerator(
        private val xmlData: List<LanguageSet>,
        private val generatedSrcPath: String,
        projectName: String
) {
    private val packageName: String = "me.jameshunt.$projectName"

    private val placeholderPattern = Regex("%[0-9]\\$[sd]")

    fun generateCode() {
        xmlData.first().generateLanguageInterface()

        xmlData.generateLanguageImplementations()

        xmlData.generateL10n()
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
                    false -> {
                        val parameters = placeHolders
                                .foldIndexed("(") { index, acc, _ -> "${acc}param$index: String, " }
                                .substringBeforeLast(",") + ")"

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

                languageSet.variables.forEach { key, value ->

                    val placeHolders = placeholderPattern.findAll(value).map { it.value }.toList()

                    when (placeHolders.isEmpty()) {
                        true -> out.writeLn("    override val $key = \"$value\"")
                        false -> {
                            val parameters = placeHolders
                                    .foldIndexed("(") { index, acc, _ -> "${acc}param$index: String, " }
                                    .substringBeforeLast(",") + ")"


                            val afterEquals = placeHolders.foldIndexed("\"$value\"") { index, acc, string ->
                                acc.replace(string, "\$param$index")
                            }


                            out.writeLn("    override fun $key$parameters: String = $afterEquals")
                        }
                    }
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

            default.variables.forEach { key, value ->

                val placeHolders = placeholderPattern.findAll(value).map { it.value }.toList()

                when (placeHolders.isEmpty()) {
                    true -> {
                        out.writeLn("    val $key: String")
                        out.writeLn("        get() = selectedLanguage.$key")
                        out.newLine()
                    }
                    false -> {
                        val parameters = placeHolders
                                .foldIndexed("(") { index, acc, _ -> "${acc}param$index: String, " }
                                .substringBeforeLast(",") + ")"

                        val parametersEnd = placeHolders
                                .foldIndexed("(") { index, acc, _ -> "${acc}param$index, " }
                                .substringBeforeLast(",") + ")"

                        out.writeLn("    fun $key$parameters: String = selectedLanguage.$key$parametersEnd")
                        out.newLine()
                    }
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

    private fun BufferedWriter.writeLn(line: String) {
        this.write(line)
        this.newLine()
    }
}