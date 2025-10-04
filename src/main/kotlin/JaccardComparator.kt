package org.example

import kotlinx.serialization.json.Json
import java.io.File

class JaccardComparator {

    data class ComparisonResult(
        val jaccardIndex: Double,
        val commonFiles: Int,
        val uniqueToFirst: Int,
        val uniqueToSecond: Int,
        val totalUniqueFiles: Int,
        val modifiedFiles: Int
    )

    fun compare(file1: File, file2: File): ComparisonResult {
        val json = Json { ignoreUnknownKeys = true }

        val metadata1 = json.decodeFromString<ArchiveMetadata>(file1.readText())
        val metadata2 = json.decodeFromString<ArchiveMetadata>(file2.readText())

        val entries1 = metadata1.entries.associateBy { it.name }
        val entries2 = metadata2.entries.associateBy { it.name }

        val allFiles = entries1.keys + entries2.keys
        val commonFiles = entries1.keys.intersect(entries2.keys)
        val uniqueToFirst = entries1.keys - entries2.keys
        val uniqueToSecond = entries2.keys - entries1.keys

        // Count modified files (same name, different CRC)
        val modifiedFiles = commonFiles.count { fileName ->
            entries1[fileName]?.crc != entries2[fileName]?.crc
        }

        val jaccardIndex = if (allFiles.isEmpty()) {
            0.0
        } else {
            commonFiles.size.toDouble() / allFiles.size.toDouble()
        }

        return ComparisonResult(
            jaccardIndex = jaccardIndex,
            commonFiles = commonFiles.size,
            uniqueToFirst = uniqueToFirst.size,
            uniqueToSecond = uniqueToSecond.size,
            totalUniqueFiles = allFiles.size,
            modifiedFiles = modifiedFiles
        )
    }

    fun printComparison(result: ComparisonResult, file1Name: String, file2Name: String) {
        println("=== Archive Comparison ===")
        println("File 1: $file1Name")
        println("File 2: $file2Name")
        println()
        println("Jaccard Index: %.4f".format(result.jaccardIndex))
        println()
        println("Statistics:")
        println("  Total unique files: ${result.totalUniqueFiles}")
        println("  Common files: ${result.commonFiles}")
        println("  Modified files (different CRC): ${result.modifiedFiles}")
        println("  Files only in first: ${result.uniqueToFirst}")
        println("  Files only in second: ${result.uniqueToSecond}")
        println()

        val overlapPercentage = (result.jaccardIndex * 100)

        println("Compared archives have: %.4f % overlap".format(overlapPercentage))
    }
}
