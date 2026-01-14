package io.github.alimsrepo.pdf.generator.content

import android.graphics.Bitmap

/**
 * Image content element
 */
data class ImageElement(
    val bitmap: Bitmap,
    val width: Float? = null,
    val height: Float? = null,
    val alignment: TextAlign = TextAlign.CENTER,
    val scaleType: ImageScaleType = ImageScaleType.FIT,
    val spacingAfter: Float = 8f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        val targetWidth = width ?: availableWidth
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()

        return when {
            height != null -> height + spacingAfter
            else -> (targetWidth * aspectRatio) + spacingAfter
        }
    }
}