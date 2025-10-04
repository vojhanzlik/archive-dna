package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

/**
 * Main entry point.
 */
fun main(args: Array<String>) {
    try {
        require(args.isNotEmpty()) {
            printUsage()
            "No arg provided"
        }

        val arg = args[0]

        when (arg) {
            "parse" -> parseMode(args)
            "compare" -> compareMode(args)
            else -> error("Unknown arg: $arg\n${printUsage()}")
        }
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}")
        exitProcess(1)
    } catch (e: Exception) {
        println("Error: ${e.message}")
        exitProcess(1)
    }
}

/**
 * Prints usage information.
 */
fun printUsage() {
    println("Usage:")
    println("  archive-dna parse <input-zip-file> <output-json-file>")
    println("    Parse a ZIP archive and save its metadata.")
    println()
    println("  archive-dna compare <json-file-1> <json-file-2>")
    println("    Compare two parsed archives.")
    println()
    println("Examples:")
    println("  archive-dna parse plugin.zip output.json")
    println("  archive-dna compare output1.json output2.json")
}

/**
 * Handles the parse arg.
 */
fun parseMode(args: Array<String>) {
    require(args.size >= 3) { "parse mode requires 2 arguments: <input-zip-file> <output-json-file>" }

    val inputFile = File(args[1])
    val outputFile = File(args[2])

    require(inputFile.exists()) { "Input file '${inputFile.absolutePath}' does not exist" }

    val archiveMetadata = ArchiveParser.parse(inputFile)

    val json = Json { prettyPrint = true }
    val jsonString = json.encodeToString(archiveMetadata)

    outputFile.writeText(jsonString)
    println("Successfully parsed archive to ${outputFile.absolutePath}")
    println("Total entries: ${archiveMetadata.entries.size}")
}

/**
 * Handles the compare arg.
 */
fun compareMode(args: Array<String>) {
    require(args.size >= 3) { "compare mode requires 2 arguments: <json-file-1> <json-file-2>" }

    val file1 = File(args[1])
    val file2 = File(args[2])

    require(file1.exists()) { "File '${file1.absolutePath}' does not exist" }
    require(file2.exists()) { "File '${file2.absolutePath}' does not exist" }

    val result = JaccardComparator.compare(file1, file2)
    JaccardComparator.printComparison(result, file1.name, file2.name)
}