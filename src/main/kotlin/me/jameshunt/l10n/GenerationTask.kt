package me.jameshunt.l10n

import java.io.BufferedWriter
import java.io.File

open class GenerationTask(private val projectName: String) {

    private val generatedSrc = "./$projectName/build/generated/source/L10n/src"

    private val packageName: String = "me.jameshunt"

    fun generateLocalizationCode() {
        //todo: check hash of strings.xml to see if it changed, and if not do nothing

        setupGeneratedSourceDirectory()

        val xmlData = getStringXmlFiles().map(::parseXml)

        xmlData.forEach { generateCode(it.first, it.second) }
        xmlData.first().second.keys.toList().generateLanguageInterface()

        xmlData.toList().generateL10n()
    }

    private fun setupGeneratedSourceDirectory() {

        val wallpaperDirectory = File(generatedSrc)
        if (wallpaperDirectory.exists()) return
        wallpaperDirectory.mkdirs()
    }

    private fun getStringXmlFiles(): List<StringFile> {
        val resPath = "./$projectName/src/main/res"
        return File(resPath)
                .list()
                .map { File("$resPath/$it") }
                .filter { it.isDirectory }
                .filter { it.name.contains("values") }
                .filter { File("${it.canonicalPath}/strings.xml").exists() }
                .map {
                    StringFile(
                            it.name.substringAfter("-", "en"),
                            File("${it.canonicalPath}/strings.xml")
                    )
                }
    }

    private fun parseXml(stringFile: StringFile): Pair<String, MutableMap<String, String>> {
        //todo proper parsing plz

        val variables = mutableMapOf<String, String>()

        stringFile.file.forEachLine {
            if (it.contains("<?xml version"))
                return@forEachLine

            val variableName = it
                    .substringAfter("\"")
                    .substringBefore("\"")
            //bad
            // what about literal " in string


            val value = it
                    .substringAfter("$variableName\">")
                    .substringBefore("<")

            when (variableName) {
                "<resources>" -> {
                }
                "</resources>" -> {
                }
                else -> variables[variableName] = value
            }
        }

        return Pair(stringFile.language, variables)
    }

    private fun generateCode(language: String, variables: Map<String, String>) {

        File("$generatedSrc/${language.toUpperCase()}.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("class ${language.toUpperCase()}: Language {")

            variables.forEach {
                out.writeLn("    override val ${it.key} = \"${it.value}\"")
            }

            out.writeLn("}")
        }
    }

    private fun List<String>.generateLanguageInterface() {
        File("$generatedSrc/Language.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("interface Language {")

            this.forEach {
                out.writeLn("    val $it: String")
            }

            out.writeLn("}")
        }
    }

    private fun List<Pair<String, MutableMap<String, String>>>.generateL10n() {
        File("$generatedSrc/L10n.kt").bufferedWriter().use { out ->
            out.writeLn("package $packageName")
            out.newLine()

            out.writeLn("import java.util.Locale")
            out.newLine()

            out.writeLn("object L10n {")

            this.first().second.keys.forEach {
                out.writeLn("    val $it: String")
                out.writeLn("        get() = androidStrings.$it")
                out.newLine()
            }

            out.writeLn("    private val androidStrings: Language by lazy {")
            out.writeLn("        when (Locale.getDefault().language) {")

            this.forEach {
                out.writeLn("            \"${it.first}\" -> ${it.first.toUpperCase()}()")
            }

            out.writeLn("            else -> throw IllegalStateException(\"what should i do here?\")")
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

data class StringFile(
        val language: String,
        val file: File
)