package io.github.alimsrepo.pdf.generator.output

import java.io.File
import java.io.OutputStream

/**
 * Output configuration
 */
sealed class PdfOutput {
    /**
     * Save to a specific file
     */
    data class ToFile(val file: File) : PdfOutput()

    /**
     * Save to a file with path and name
     */
    data class ToPath(val directoryPath: String, val fileName: String) : PdfOutput()

    /**
     * Return as byte array (useful for sharing/uploading)
     */
    data object ToByteArray : PdfOutput()

    /**
     * Write to an OutputStream (useful for network streams, ContentResolver, etc.)
     */
    data class ToOutputStream(val outputStream: OutputStream) : PdfOutput()
}

