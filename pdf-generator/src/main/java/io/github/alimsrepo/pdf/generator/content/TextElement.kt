package io.github.alimsrepo.pdf.generator.content

import android.graphics.Paint
import android.graphics.Typeface

/**
 * Text content element
 */
data class TextElement(
    val text: String,
    val textSize: Float = 12f,
    val textColor: Int = 0xFF000000.toInt(),
    val typeface: Typeface = Typeface.DEFAULT,
    val alignment: TextAlign = TextAlign.LEFT,
    val lineSpacing: Float = 1.2f,
    val paragraphSpacing: Float = 8f,
    val maxLines: Int = Int.MAX_VALUE,
    val indent: Float = 0f
) : PdfElement {

    private val paint: Paint by lazy {
        Paint().apply {
            this.textSize = this@TextElement.textSize
            this.typeface = this@TextElement.typeface
            this.color = textColor
            isAntiAlias = true
        }
    }

    override fun measureHeight(availableWidth: Float): Float {
        if (text.isEmpty()) return 0f

        val effectiveWidth = availableWidth - indent
        val lines = wrapText(text, effectiveWidth, paint)
        val lineHeight = textSize * lineSpacing

        return (lines.size.coerceAtMost(maxLines) * lineHeight) + paragraphSpacing
    }

    companion object {
        fun wrapText(text: String, maxWidth: Float, paint: Paint): List<String> {
            val lines = mutableListOf<String>()
            val paragraphs = text.split("\n")

            for (paragraph in paragraphs) {
                if (paragraph.isEmpty()) {
                    lines.add("")
                    continue
                }

                val words = paragraph.split(" ")
                var currentLine = StringBuilder()

                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val width = paint.measureText(testLine)

                    if (width <= maxWidth) {
                        currentLine = StringBuilder(testLine)
                    } else {
                        if (currentLine.isNotEmpty()) {
                            lines.add(currentLine.toString())
                        }
                        currentLine = StringBuilder(word)
                    }
                }

                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                }
            }

            return lines
        }
    }
}