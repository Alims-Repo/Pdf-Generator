package io.github.alimsrepo.pdf.generator.content

/**
 * Horizontal line/divider element
 */
data class DividerElement(
    val thickness: Float = 1f,
    val color: Int = 0xFF000000.toInt(),
    val marginTop: Float = 8f,
    val marginBottom: Float = 8f,
    val dashWidth: Float = 0f,
    val dashGap: Float = 0f
) : PdfElement {
    override fun measureHeight(availableWidth: Float): Float = thickness + marginTop + marginBottom
}