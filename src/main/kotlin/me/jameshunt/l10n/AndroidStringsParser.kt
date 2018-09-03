package me.jameshunt.l10n

import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import java.io.File


class AndroidStringsParser(private val projectName: String) {

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
        val serializer = Persister()
        val xmlResult = serializer.read(Resources::class.java, stringFile.file)
        val map = xmlResult.list.map { Pair(it.name, it.value.escapeCharacters()) }.toMap()

        return LanguageSet(stringFile.language, map)
    }

    private fun String.escapeCharacters(): String {
        return this.escapeBackslash().escapeDoubleQuotes()
    }

    private fun String.escapeBackslash(): String {
        return this.replace("\\","\\\\")
    }

    private fun String.escapeDoubleQuotes(): String {
        return this.replace("\"","\\\"")
    }
}


@Root
private data class Resources @JvmOverloads constructor(

        @field:ElementList(inline=true)
        var list: MutableList<AndroidString> = mutableListOf()
)

@Root(name = "string")
private data class AndroidString @JvmOverloads constructor(

        @field:Attribute
        var name: String = "",

        @field:Text
        var value: String = ""
)

private data class StringFile(
        val language: String,
        val file: File
)

data class LanguageSet(
        val language: String,
        val variables: Map<String, String>
)