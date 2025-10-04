package org.example

import kotlinx.serialization.json.Json
import java.io.File

/**
 * Compares two archive metadata files using Jaccard Index.
 */
object JaccardComparator {

    /**
     * Holds the results of comparing two archives.
     */
    data class ComparisonResult(
        val jaccardIndex: Double,
        val totalUniqueHashes: Int,
        val commonHashes: Int,
        val uniqueInFirst: Int,
        val uniqueInSecond: Int,
    )

    /**
     * Compares two archive metadata JSON files using hash-based Jaccard Index.
     */
    fun compare(file1: File, file2: File): ComparisonResult {
        require(file1.exists()) { "File '${file1.absolutePath}' does not exist" }
        require(file2.exists()) { "File '${file2.absolutePath}' does not exist" }
        require(file1.extension == "json") { "File '${file1.name}' must be a JSON file" }
        require(file2.extension == "json") { "File '${file2.name}' must be a JSON file" }

        val json = Json { ignoreUnknownKeys = true }

        val metadata1 = json.decodeFromString<ArchiveMetadata>(file1.readText())
        val metadata2 = json.decodeFromString<ArchiveMetadata>(file2.readText())

        val hashes1 = metadata1.entries.map { it.computeHash() }.toSet()
        val hashes2 = metadata2.entries.map { it.computeHash() }.toSet()

        // Jaccard Index based on hash sets
        val allHashes = hashes1 + hashes2
        val commonHashes = hashes1.intersect(hashes2)
        val uniqueInFirst = hashes1 - hashes2
        val uniqueInSecond = hashes2 - hashes1

        val jaccardIndex = if (allHashes.isEmpty()) {
            0.0
        } else {
            commonHashes.size.toDouble() / allHashes.size.toDouble()
        }


        return ComparisonResult(
            jaccardIndex = jaccardIndex,
            totalUniqueHashes = allHashes.size,
            commonHashes = commonHashes.size,
            uniqueInFirst = uniqueInFirst.size,
            uniqueInSecond = uniqueInSecond.size
        )
    }

    /**
     * Prints the comparison results.
     */
    fun printComparison(result: ComparisonResult, file1Name: String, file2Name: String) {
        println("=== Archive Comparison ===")
        println("File 1: $file1Name")
        println("File 2: $file2Name")
        println()
        println("Jaccard Index: %.2f".format(result.jaccardIndex))
        println()
        println("  Total unique hashes: ${result.totalUniqueHashes}")
        println("  Common hashes (identical entries): ${result.commonHashes}")
        println("  Hashes only in first: ${result.uniqueInFirst}")
        println("  Hashes only in second: ${result.uniqueInSecond}")
        println()

        val overlapPercentage = (result.jaccardIndex * 100)
        println("Compared archives have: %.2f%%".format(overlapPercentage))

    }
}
