package org.example

import kotlinx.serialization.Serializable

@Serializable
data class ArchiveMetadata(
    val archiveName: String,
    val archiveSize: Long,
    val totalEntries: Int,
    val entries: List<Entry>
) {
    @Serializable
    data class Entry(
        val name: String,
        val size: Long,
        val compressedSize: Long,
        val crc: Long,
        val time: Long,
    )
}
