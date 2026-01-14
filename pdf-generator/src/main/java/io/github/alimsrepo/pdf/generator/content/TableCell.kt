package io.github.alimsrepo.pdf.generator.content

import android.graphics.Typeface

/**
 * Table cell configuration
 */
data class TableCell(
    val content: String,
    val textSize: Float = 11f,
    val textColor: Int = 0xFF000000.toInt(),
    val backgroundColor: Int? = null,
    val typeface: Typeface = Typeface.DEFAULT,
    val alignment: TextAlign = TextAlign.LEFT,
    val padding: Float = 4f,
    val colSpan: Int = 1,
    val rowSpan: Int = 1
)