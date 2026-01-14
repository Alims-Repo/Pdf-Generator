package io.github.alimsrepo.pdf.generator.config

import android.graphics.Bitmap
import android.graphics.Typeface

/**
 * Watermark configuration for PDF pages
 */
sealed class Watermark {

    /**
     * Text-based watermark
     */
    data class Text(
        val text: String,
        val textSize: Float = 48f,
        val textColor: Int = 0x33000000, // Semi-transparent black
        val typeface: Typeface = Typeface.DEFAULT_BOLD,
        val rotation: Float = -45f,
        val position: WatermarkPosition = WatermarkPosition.CENTER,
        val repeatPattern: Boolean = false
    ) : Watermark()

    /**
     * Image-based watermark
     */
    data class Image(
        val bitmap: Bitmap,
        val alpha: Float = 0.2f, // 0.0 - 1.0
        val scale: Float = 0.5f, // Scale relative to page
        val position: WatermarkPosition = WatermarkPosition.CENTER,
        val repeatPattern: Boolean = false
    ) : Watermark()

    companion object {
        /**
         * Create a "CONFIDENTIAL" watermark
         */
        fun confidential(color: Int = 0x33FF0000) = Text(
            text = "CONFIDENTIAL",
            textColor = color
        )

        /**
         * Create a "DRAFT" watermark
         */
        fun draft(color: Int = 0x33000000) = Text(
            text = "DRAFT",
            textColor = color
        )

        /**
         * Create a "COPY" watermark
         */
        fun copy(color: Int = 0x33000000) = Text(
            text = "COPY",
            textColor = color
        )

        /**
         * Create a custom text watermark
         */
        fun text(
            text: String,
            textSize: Float = 48f,
            textColor: Int = 0x33000000,
            rotation: Float = -45f
        ) = Text(
            text = text,
            textSize = textSize,
            textColor = textColor,
            rotation = rotation
        )
    }
}

/**
 * Position options for watermarks
 */
enum class WatermarkPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT
}

