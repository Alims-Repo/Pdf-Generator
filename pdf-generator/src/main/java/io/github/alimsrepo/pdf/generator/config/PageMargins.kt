package io.github.alimsrepo.pdf.generator.config

/**
 * Page margins configuration with values in points (72 points = 1 inch)
 */
data class PageMargins(
    val top: Float,
    val bottom: Float,
    val left: Float,
    val right: Float
) {
    /**
     * Calculate the usable content width after margins
     */
    fun contentWidth(pageWidth: Float): Float = pageWidth - left - right

    /**
     * Calculate the usable content height after margins
     */
    fun contentHeight(pageHeight: Float): Float = pageHeight - top - bottom

    companion object {
        /**
         * No margins
         */
        val NONE = PageMargins(0f, 0f, 0f, 0f)

        /**
         * Normal margins (1 inch / 72 points all around)
         */
        val NORMAL = PageMargins(72f, 72f, 72f, 72f)

        /**
         * Narrow margins (0.5 inch / 36 points all around)
         */
        val NARROW = PageMargins(36f, 36f, 36f, 36f)

        /**
         * Wide margins (1.5 inch / 108 points all around)
         */
        val WIDE = PageMargins(108f, 108f, 108f, 108f)

        /**
         * Moderate margins (0.75 inch / 54 points all around)
         */
        val MODERATE = PageMargins(54f, 54f, 54f, 54f)

        /**
         * Create symmetric margins
         * @param horizontal Left and right margin in points
         * @param vertical Top and bottom margin in points
         */
        fun symmetric(horizontal: Float, vertical: Float): PageMargins {
            return PageMargins(vertical, vertical, horizontal, horizontal)
        }

        /**
         * Create uniform margins (same on all sides)
         * @param margin Margin value in points
         */
        fun uniform(margin: Float): PageMargins {
            return PageMargins(margin, margin, margin, margin)
        }

        /**
         * Create margins from millimeters
         */
        fun fromMm(top: Float, bottom: Float, left: Float, right: Float): PageMargins {
            return PageMargins(
                top = mmToPoints(top),
                bottom = mmToPoints(bottom),
                left = mmToPoints(left),
                right = mmToPoints(right)
            )
        }

        /**
         * Create margins from inches
         */
        fun fromInches(top: Float, bottom: Float, left: Float, right: Float): PageMargins {
            return PageMargins(
                top = inchesToPoints(top),
                bottom = inchesToPoints(bottom),
                left = inchesToPoints(left),
                right = inchesToPoints(right)
            )
        }

        private fun mmToPoints(mm: Float): Float = mm * 2.83465f
        private fun inchesToPoints(inches: Float): Float = inches * 72f
    }
}

