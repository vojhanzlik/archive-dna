package org.example

import kotlinx.serialization.Serializable

/**
 * Metadata for a parsed archive containing file entries.
 */
@Serializable
data class ArchiveMetadata(
    val archiveName: String,
    val archiveSize: Long,
    val totalEntries: Int,
    val entries: List<Entry>
) {
    /**
     * Metadata for a single file entry in the archive.
     */
    @Serializable
    data class Entry(
        val name: String,
        val size: Long,
        val compressedSize: Long,
        val crc: Long,
        val time: Long,
    ) {
        /**
         * Computes a hash based on name, size, and checksum.
         */
        fun computeHash(): String {
            val data = "$name|$size|$crc"
            return data.hashCode().toString(16)
        }
    }
}
