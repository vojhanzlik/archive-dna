package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: archive-dna <input-zip-file> <output-json-file>")
        println("Example: archive-dna plugin.zip output.json")
        exitProcess(1)
    }

    val inputFile = File(args[0])
    val outputFile = File(args[1])

    try {
        val parser = ArchiveParser()
        val fingerprint = parser.parse(inputFile)

        val json = Json { prettyPrint = true }
        val jsonString = json.encodeToString(fingerprint)

        outputFile.writeText(jsonString)
        println("Successfully parsed archive to ${outputFile.absolutePath}")
    } catch (e: Exception) {
        println("Error processing archive: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
}