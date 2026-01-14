package io.github.alimsrepo.pdf.generator.output

import java.io.File

/**
 * Result of successful PDF generation
 */
sealed class PdfResult {
    /**
     * PDF was written to a file
     */
    data class FileResult(val file: File) : PdfResult()

    /**
     * PDF was generated as a byte array
     */
    data class ByteArrayResult(val bytes: ByteArray) : PdfResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ByteArrayResult) return false
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }

    /**
     * PDF was written to an OutputStream
     */
    data class StreamResult(val bytesWritten: Long) : PdfResult()
}