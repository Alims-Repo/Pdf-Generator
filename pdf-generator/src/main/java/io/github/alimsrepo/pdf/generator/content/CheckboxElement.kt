package io.github.alimsrepo.pdf.generator.content

import android.graphics.Typeface

/**
 * Checkbox element for forms and checklists
 */
data class CheckboxElement(
    val label: String,
    val isChecked: Boolean = false,
    val textSize: Float = 12f,
    val textColor: Int = 0xFF000000.toInt(),
    val checkboxSize: Float = 14f,
    val checkboxColor: Int = 0xFF000000.toInt(),
    val checkmarkColor: Int = 0xFF000000.toInt(),
    val typeface: Typeface = Typeface.DEFAULT,
    val spacingAfter: Float = 4f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        return maxOf(checkboxSize, textSize * 1.2f) + spacingAfter
    }
}

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

/**
 * Checkbox item data
 */
data class CheckboxItem(
    val label: String,
    val isChecked: Boolean = false
)

