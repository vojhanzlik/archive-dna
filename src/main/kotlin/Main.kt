package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>) {
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
}

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

fun parseMode(args: Array<String>) {
    require(args.size >= 3) { "parse mode requires 2 arguments: <input-zip-file> <output-json-file>" }

    val inputFile = File(args[1])
    val outputFile = File(args[2])

    require(inputFile.exists()) { "Input file '${inputFile.absolutePath}' does not exist" }

    val parser = ArchiveParser()
    val archiveMetadata = parser.parse(inputFile)

    val json = Json { prettyPrint = true }
    val jsonString = json.encodeToString(archiveMetadata)

    outputFile.writeText(jsonString)
    println("Successfully parsed archive to ${outputFile.absolutePath}")
    println("Total entries: ${archiveMetadata.entries.size}")
}

fun compareMode(args: Array<String>) {
    require(args.size >= 3) { "compare mode requires 2 arguments: <json-file-1> <json-file-2>" }

    val file1 = File(args[1])
    val file2 = File(args[2])

    require(file1.exists()) { "File '${file1.absolutePath}' does not exist" }
    require(file2.exists()) { "File '${file2.absolutePath}' does not exist" }

    val comparator = JaccardComparator()
    val result = comparator.compare(file1, file2)
    comparator.printComparison(result, file1.name, file2.name)
}