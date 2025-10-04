package org.example

import java.io.File
import java.util.zip.ZipFile
import kotlin.system.exitProcess

class ArchiveParser {
    fun parse(archiveFile: File): ArchiveMetadata {

        if (!archiveFile.exists()) {
            println("Input file '${archiveFile.absolutePath}' does not exist")
            exitProcess(1)
        }

        if (!archiveFile.name.endsWith(".zip", ignoreCase = true)) {
            println("Input file does not have .zip extension")
        }

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
