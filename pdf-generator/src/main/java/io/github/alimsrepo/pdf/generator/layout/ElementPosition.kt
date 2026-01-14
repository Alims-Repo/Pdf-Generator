package io.github.alimsrepo.pdf.generator.layout

import io.github.alimsrepo.pdf.generator.content.PdfElement

/**
 * Element with its position on the page
 */
data class ElementPosition(
    val element: PdfElement,
    val x: Float,
    val y: Float,
    val availableWidth: Float
)