package io.github.alimsrepo.pdf.generator.content

/**
 * Box/Container element for bordered content blocks with padding and background
 */
data class BoxElement(
    val elements: List<PdfElement>,
    val padding: Float = 12f,
    val backgroundColor: Int? = null,
    val borderWidth: Float = 1f,
    val borderColor: Int = 0xFF000000.toInt(),
    val borderRadius: Float = 0f,
    val spacingAfter: Float = 8f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        val contentWidth = availableWidth - (padding * 2) - (borderWidth * 2)
        var totalHeight = padding * 2 + borderWidth * 2

        for (element in elements) {
            totalHeight += element.measureHeight(contentWidth)
        }

        return totalHeight + spacingAfter
    }

    companion object {
        /**
         * Create a callout box with light background
         */
        fun callout(
            elements: List<PdfElement>,
            backgroundColor: Int = 0xFFF5F5F5.toInt(),
            borderColor: Int = 0xFFDDDDDD.toInt()
        ) = BoxElement(
            elements = elements,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            borderRadius = 4f
        )

        /**
         * Create an info box (blue tint)
         */
        fun info(elements: List<PdfElement>) = BoxElement(
            elements = elements,
            backgroundColor = 0xFFE3F2FD.toInt(),
            borderColor = 0xFF2196F3.toInt(),
            borderRadius = 4f
        )

        /**
         * Create a warning box (yellow tint)
         */
        fun warning(elements: List<PdfElement>) = BoxElement(
            elements = elements,
            backgroundColor = 0xFFFFF8E1.toInt(),
            borderColor = 0xFFFFC107.toInt(),
            borderRadius = 4f
        )

        /**
         * Create an error box (red tint)
         */
        fun error(elements: List<PdfElement>) = BoxElement(
            elements = elements,
            backgroundColor = 0xFFFFEBEE.toInt(),
            borderColor = 0xFFF44336.toInt(),
            borderRadius = 4f
        )

        /**
         * Create a success box (green tint)
         */
        fun success(elements: List<PdfElement>) = BoxElement(
            elements = elements,
            backgroundColor = 0xFFE8F5E9.toInt(),
            borderColor = 0xFF4CAF50.toInt(),
            borderRadius = 4f
        )
    }
}

