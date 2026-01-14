package io.github.alimsrepo.pdf.generator.content

/**
 * Table row configuration
 */
data class TableRow(
    val cells: List<TableCell>,
    val isHeader: Boolean = false,
    val minHeight: Float = 0f
)
