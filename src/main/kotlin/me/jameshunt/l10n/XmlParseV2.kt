//package me.jameshunt.l10n
//
//import java.io.File





//class XmlParseV2(private val projectName: String) {
//
//    fun getXmlData() {
//        return getFiles().forEach { parseXml(it) }
//    }
//
//    private fun getFiles(): List<StringFileV2> {
//        val resPath = "./$projectName/src/main/res"
//
//        println(File(resPath).exists())
//        println(File(resPath).absolutePath)
//
//        return File(resPath)
//                .list()
//                .map { File("$resPath/$it") }
//                .filter { it.isDirectory }
//                .filter { it.name.contains("values") }
//                .filter { File("${it.canonicalPath}/strings.xml").exists() }
//                .map {
//
//                    val language = it.name.substringAfter("-", "default")
//
//                    StringFileV2(
//                            language,
//                            File("${it.canonicalPath}/strings.xml")
//                    )
//                }
//    }
//
//    private fun parseXml(stringFileV2: StringFileV2) {
//
//        println("parse: ${stringFileV2.file.absolutePath}")

//        val serializer = Persister()
//        val example = serializer.read(Resources::class.java, stringFileV2.file)
//
//        example.map.forEach {
//            print(it.key)
//        }
//
//        return LanguageSet(stringFileV2.language, example.map)
//    }
//}

//@Root
//class Resources {
//
//    @ElementMap(entry = "string", key = "name", attribute = true, inline = true)
//    lateinit var map: Map<String, String>
//
//}

//private data class StringFileV2(
//        val language: String,
//        val file: File
//)