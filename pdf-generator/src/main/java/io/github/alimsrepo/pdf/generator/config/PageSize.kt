package io.github.alimsrepo.pdf.generator.config

/**
 * Standard paper sizes with dimensions in points (72 points = 1 inch)
 */
enum class PageSize(
    val widthPt: Float,
    val heightPt: Float
) {
    // ISO A Series
    A3(841.89f, 1190.55f),      // 297mm x 420mm
    A4(595.28f, 841.89f),       // 210mm x 297mm
    A5(419.53f, 595.28f),       // 148mm x 210mm
    A6(297.64f, 419.53f),       // 105mm x 148mm

    // ISO B Series
    B4(708.66f, 1000.63f),      // 250mm x 353mm
    B5(498.90f, 708.66f),       // 176mm x 250mm

    // US Sizes
    LETTER(612f, 792f),          // 8.5" x 11"
    LEGAL(612f, 1008f),          // 8.5" x 14"
    TABLOID(792f, 1224f),        // 11" x 17"
    EXECUTIVE(522f, 756f);       // 7.25" x 10.5"

    companion object {
        /**
         * Create a custom page size with specific dimensions
         * @param widthMm Width in millimeters
         * @param heightMm Height in millimeters
         */
        fun customMm(widthMm: Float, heightMm: Float): CustomPageSize {
            return CustomPageSize(
                widthPt = mmToPoints(widthMm),
                heightPt = mmToPoints(heightMm)
            )
        }

        /**
         * Create a custom page size with specific dimensions in inches
         * @param widthInches Width in inches
         * @param heightInches Height in inches
         */
        fun customInches(widthInches: Float, heightInches: Float): CustomPageSize {
            return CustomPageSize(
                widthPt = inchesToPoints(widthInches),
                heightPt = inchesToPoints(heightInches)
            )
        }

        /**
         * Create a custom page size with specific dimensions in points
         * @param widthPt Width in points
         * @param heightPt Height in points
         */
        fun customPoints(widthPt: Float, heightPt: Float): CustomPageSize {
            return CustomPageSize(widthPt, heightPt)
        }

        private fun mmToPoints(mm: Float): Float = mm * 2.83465f
        private fun inchesToPoints(inches: Float): Float = inches * 72f
    }
}