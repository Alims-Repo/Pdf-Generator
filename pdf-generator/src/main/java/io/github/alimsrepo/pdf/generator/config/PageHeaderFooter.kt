package io.github.alimsrepo.pdf.generator.config

/**
 * Configuration for page headers and footers
 */
data class PageHeaderFooter(
    val enabled: Boolean = false,
    val height: Float = 40f,
    val content: HeaderFooterContent? = null
)