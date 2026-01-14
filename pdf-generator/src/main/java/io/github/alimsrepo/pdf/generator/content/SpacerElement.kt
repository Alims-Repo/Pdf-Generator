package io.github.alimsrepo.pdf.generator.content

/**
 * Spacer element for adding vertical space
 */
data class SpacerElement(
    val height: Float
) : PdfElement {
    override fun measureHeight(availableWidth: Float): Float = height
}