package io.github.alimsrepo.pdf.generator.content

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Base interface for all PDF content elements
 */
sealed interface PdfElement {
    /**
     * Measure the height this element needs given a specific width
     */
    fun measureHeight(availableWidth: Float): Float
}