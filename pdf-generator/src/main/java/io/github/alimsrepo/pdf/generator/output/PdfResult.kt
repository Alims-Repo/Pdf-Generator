package io.github.alimsrepo.pdf.generator.output

import java.io.File

/**
 * Result of successful PDF generation
 */
sealed class PdfResult {
    data class FileResult(val file: File) : PdfResult()
    data class ByteArrayResult(val bytes: ByteArray) : PdfResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ByteArrayResult) return false
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }
}