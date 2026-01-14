package io.github.alimsrepo.pdf.generator.layout

/**
 * Represents content that belongs to a specific page
 */
data class PageContent(
    val pageNumber: Int,
    val elements: List<ElementPosition>
)