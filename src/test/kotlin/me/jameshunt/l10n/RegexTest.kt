package me.jameshunt.l10n

import org.junit.Test

class RegexTest {

    @Test
    fun findPlaceholders() {
        val pattern = Regex("%[0-9]\\$[sd]")
        val testString = "Hello, %1\$s! You have %2\$d new messages."

        val testString2 = "All items in %1\$s were deleted"

        pattern.findAll(testString2).forEach {
            println(it.value)
        }
    }
}