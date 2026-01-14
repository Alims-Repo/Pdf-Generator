package io.github.alimsrepo.pdf.generator.content

import android.graphics.Paint
import android.graphics.Typeface

/**
 * Special list element that maintains numbering offset for split lists
 */
data class NumberedListChunk(
    val items: List<String>,
    val startNumber: Int,
    val textSize: Float = 12f,
    val textColor: Int = 0xFF000000.toInt(),
    val typeface: Typeface = Typeface.DEFAULT,
    val indent: Float = 20f,
    val itemSpacing: Float = 4f,
    val spacingAfter: Float = 8f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        val paint = Paint().apply {
            this.textSize = this@NumberedListChunk.textSize
            this.typeface = this@NumberedListChunk.typeface
            isAntiAlias = true
        }

        var totalHeight = 0f
        val effectiveWidth = availableWidth - indent

        items.forEach { item ->
            val lines = TextElement.wrapText(item, effectiveWidth, paint)
            totalHeight += (lines.size * textSize * 1.2f) + itemSpacing
        }

        return totalHeight + spacingAfter
    }
}