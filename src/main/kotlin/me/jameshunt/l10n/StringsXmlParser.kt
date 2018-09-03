package me.jameshunt.l10n

import java.io.File

class StringsXmlParser(private val projectName: String) {


    fun getXmlData(): List<LanguageSet> {
        return getFiles().map { parseXml(it) }
    }

    private fun getFiles(): List<StringFile> {
        val resPath = "./$projectName/src/main/res"
        return File(resPath)
                .list()
                .map { File("$resPath/$it") }
                .filter { it.isDirectory }
                .filter { it.name.contains("values") }
                .filter { File("${it.canonicalPath}/strings.xml").exists() }
                .map {

                    val language = it.name.substringAfter("-", "default")

                    StringFile(
                            language,
                            File("${it.canonicalPath}/strings.xml")
                    )
                }
    }

    private fun parseXml(stringFile: StringFile): LanguageSet {
        //todo proper parsing plz

        val variables = mutableMapOf<String, String>()

        stringFile.file.forEachLine {
            if(it.isBlank())
                return@forEachLine

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

        return LanguageSet(stringFile.language, variables)
    }
}

private data class StringFile(
        val language: String,
        val file: File
)

data class LanguageSet(
        val language: String,
        val variables: Map<String, String>
)