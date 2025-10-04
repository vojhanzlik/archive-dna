package org.example

import java.io.File
import java.util.zip.ZipFile

/**
 * Parses ZIP archives and extracts metadata from source files.
 */
object ArchiveParser {
    /**
     * Parses a ZIP archive and extracts metadata for .java and .kt files.
     */
    fun parse(archiveFile: File): ArchiveMetadata {
        require(archiveFile.exists()) { "Input file '${archiveFile.absolutePath}' does not exist" }

        require(archiveFile.name.endsWith(".zip", ignoreCase = true)) { "Input file must have .zip extension" }

        ZipFile(archiveFile).use { zipFile ->
            val entries = zipFile.entries().toList()
                .filter { zipEntry ->
                    !zipEntry.isDirectory &&
                    (zipEntry.name.endsWith(".java") || zipEntry.name.endsWith(".kt"))
                }
                .map { zipEntry ->
                    ArchiveMetadata.Entry(
                        name = zipEntry.name,
                        size = zipEntry.size,
                        compressedSize = zipEntry.compressedSize,
                        crc = zipEntry.crc,
                        time = zipEntry.time,
                    )
                }

            return ArchiveMetadata(
                archiveName = archiveFile.name,
                archiveSize = archiveFile.length(),
                totalEntries = entries.size,
                entries = entries
            )
        }
    }
}
