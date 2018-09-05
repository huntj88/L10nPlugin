package me.jameshunt.l10n

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory


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
        val parser = SAXParserFactory.newInstance().newSAXParser()
        val xmlHandler = AndroidStringsHandler()

        parser.parse(stringFile.file, xmlHandler)

        return LanguageSet(stringFile.language, xmlHandler.androidStrings)
    }
}


private class AndroidStringsHandler : DefaultHandler() {

    /**
     * this class could use some work
     * needs better warnings for unsupported stuff
     */

    val androidStrings = mutableMapOf<String, String>()

    var key = ""
    var accumulator = StringBuffer()

    var ableToParse = false

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName.toLowerCase()) {
            "resources" -> {}
            "string" -> {
                ableToParse = true
                accumulator.setLength(0)
                this.key = attributes.getValue(0)
            }
            else -> {
                ableToParse = false
            }
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        accumulator.append(ch, start, length)
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            "resources" -> {}
            "string" -> {
                if (ableToParse) {
                    androidStrings[this.key] = accumulator.toString().trim().escapeCharacters()
                } else {
                    println("string contains features not supported: $key")
                }
            }
            else -> {
                println("strings.xml feature not supported: <$qName>")
            }
        }
    }

    private fun String.escapeCharacters(): String {
        return this.escapeDoubleQuotes().escapeNewLine()
    }

    private fun String.escapeDoubleQuotes(): String {
        return this.replace("\"", "\\\"")
    }

    private fun String.escapeNewLine(): String {
        return this.replace(Regex("(\r\n|\r|\n)[ ]+"), "\\\\n").replace("\n", " ")
    }

    override fun warning(exception: SAXParseException) {
        println("WARNING: line ${exception.lineNumber}: ${exception.message}")
    }

    override fun error(exception: SAXParseException) {
        println("ERROR: line ${exception.lineNumber}: ${exception.message}")
    }

    @Throws(SAXException::class)
    override fun fatalError(exception: SAXParseException) {
        println("FATAL: line ${exception.lineNumber}: ${exception.message}")
        throw exception
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