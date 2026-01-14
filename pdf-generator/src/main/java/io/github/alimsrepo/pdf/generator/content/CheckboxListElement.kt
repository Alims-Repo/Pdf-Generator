package io.github.alimsrepo.pdf.generator.content

import android.graphics.Typeface

/**
 * Checkbox list element for multiple checkboxes
 */
data class CheckboxListElement(
    val items: List<CheckboxItem>,
    val textSize: Float = 12f,
    val textColor: Int = 0xFF000000.toInt(),
    val checkboxSize: Float = 14f,
    val checkboxColor: Int = 0xFF000000.toInt(),
    val checkmarkColor: Int = 0xFF000000.toInt(),
    val typeface: Typeface = Typeface.DEFAULT,
    val itemSpacing: Float = 4f,
    val spacingAfter: Float = 8f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        val itemHeight = maxOf(checkboxSize, textSize * 1.2f) + itemSpacing
        return (items.size * itemHeight) + spacingAfter
    }
}