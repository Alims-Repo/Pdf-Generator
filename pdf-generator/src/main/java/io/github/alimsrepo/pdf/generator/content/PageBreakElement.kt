package io.github.alimsrepo.pdf.generator.content


/**
 * Page break element - forces content to start on a new page
 */
object PageBreakElement : PdfElement {
    override fun measureHeight(availableWidth: Float): Float = Float.MAX_VALUE
}