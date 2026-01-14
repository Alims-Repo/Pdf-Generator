package io.github.alimsrepo.pdf.generator.content

import android.graphics.Paint


/**
 * Table element for tabular data
 */
data class TableElement(
    val rows: List<TableRow>,
    val columnWidths: List<Float>? = null,
    val borderWidth: Float = 0.5f,
    val borderColor: Int = 0xFF000000.toInt(),
    val headerBackgroundColor: Int = 0xFFEEEEEE.toInt(),
    val alternateRowColor: Int? = null,
    val spacingAfter: Float = 8f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        val colWidths = calculateColumnWidths(availableWidth)
        var totalHeight = 0f

        for (row in rows) {
            totalHeight += measureRowHeight(row, colWidths)
        }

        return totalHeight + spacingAfter
    }

    private fun measureRowHeight(row: TableRow, colWidths: List<Float>): Float {
        val paint = Paint().apply {
            textSize = row.cells.firstOrNull()?.textSize ?: 11f
            isAntiAlias = true
        }

        var maxHeight = row.minHeight.coerceAtLeast(24f)

        row.cells.forEachIndexed { index, cell ->
            if (index < colWidths.size) {
                paint.textSize = cell.textSize
                paint.typeface = cell.typeface

                val cellWidth = colWidths[index] - (cell.padding * 2)
                val lines = TextElement.wrapText(cell.content, cellWidth, paint)
                val cellHeight = (lines.size * cell.textSize * 1.2f) + (cell.padding * 2)
                maxHeight = maxOf(maxHeight, cellHeight)
            }
        }

        return maxHeight
    }

    fun calculateColumnWidths(availableWidth: Float): List<Float> {
        if (columnWidths != null) return columnWidths

        val maxColumns = rows.maxOfOrNull { it.cells.size } ?: 1
        val equalWidth = availableWidth / maxColumns
        return List(maxColumns) { equalWidth }
    }
}