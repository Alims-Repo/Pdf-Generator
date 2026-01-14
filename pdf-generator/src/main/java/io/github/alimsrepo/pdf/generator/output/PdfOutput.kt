package io.github.alimsrepo.pdf.generator.output

import java.io.File

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
    object ToByteArray : PdfOutput()
}

