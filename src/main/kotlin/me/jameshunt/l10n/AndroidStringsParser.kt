package me.jameshunt.l10n

import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import java.io.File


class AndroidStringsParser(private val projectName: String) {

    fun getXmlData(): List<LanguageSet> {
        return getFiles().map { parseXml(it) }
    }

    private fun getFiles(): List<StringFileV2> {
        val resPath = "./$projectName/src/main/res"

        return File(resPath)
                .list()
                .map { File("$resPath/$it") }
                .filter { it.isDirectory }
                .filter { it.name.contains("values") }
                .filter { File("${it.canonicalPath}/strings.xml").exists() }
                .map {

                    val language = it.name.substringAfter("-", "default")

                    StringFileV2(
                            language,
                            File("${it.canonicalPath}/strings.xml")
                    )
                }
    }

    private fun parseXml(stringFileV2: StringFileV2): LanguageSet {
        val serializer = Persister()
        val xmlResult = serializer.read(Resources::class.java, stringFileV2.file)
        val map = xmlResult.list.map { Pair(it.name, it.value) }.toMap()

        return LanguageSet(stringFileV2.language, map)
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

private data class StringFileV2(
        val language: String,
        val file: File
)

data class LanguageSet(
        val language: String,
        val variables: Map<String, String>
)