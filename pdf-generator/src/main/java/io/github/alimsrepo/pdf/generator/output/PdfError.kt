package io.github.alimsrepo.pdf.generator.output

/**
 * Error during PDF generation
 */
data class PdfError(
    val message: String,
    val exception: Throwable? = null
)