package me.jameshunt.l10n.generate

import java.io.BufferedWriter

val placeholderPattern = Regex("%[0-9]\\$[sd]")

internal fun BufferedWriter.writeLn(line: String) {
    this.write(line)
    this.newLine()
}