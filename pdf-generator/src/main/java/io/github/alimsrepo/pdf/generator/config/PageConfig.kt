package io.github.alimsrepo.pdf.generator.config

/**
 * Complete page configuration
 */
data class PageConfig(
    val pageSize: PageSize = PageSize.A4,
    val customPageSize: CustomPageSize? = null,
    val orientation: PageOrientation = PageOrientation.PORTRAIT,
    val margins: PageMargins = PageMargins.Companion.NORMAL,
    val header: PageHeaderFooter = PageHeaderFooter(),
    val footer: PageHeaderFooter = PageHeaderFooter(),
    val backgroundColor: Int = 0xFFFFFFFF.toInt()
) {
    /**
     * Get the actual page width in points based on size and orientation
     */
    val pageWidth: Float
        get() {
            val width = customPageSize?.widthPt ?: pageSize.widthPt
            val height = customPageSize?.heightPt ?: pageSize.heightPt
            return if (orientation == PageOrientation.LANDSCAPE) height else width
        }

    /**
     * Get the actual page height in points based on size and orientation
     */
    val pageHeight: Float
        get() {
            val width = customPageSize?.widthPt ?: pageSize.widthPt
            val height = customPageSize?.heightPt ?: pageSize.heightPt
            return if (orientation == PageOrientation.LANDSCAPE) width else height
        }

    /**
     * Get the usable content width after margins
     */
    val contentWidth: Float
        get() = margins.contentWidth(pageWidth)

    /**
     * Get the usable content height after margins, headers, and footers
     */
    val contentHeight: Float
        get() {
            var height = margins.contentHeight(pageHeight)
            if (header.enabled) height -= header.height
            if (footer.enabled) height -= footer.height
            return height
        }

    /**
     * Get the Y position where content should start
     */
    val contentStartY: Float
        get() = margins.top + if (header.enabled) header.height else 0f

    /**
     * Get the X position where content should start
     */
    val contentStartX: Float
        get() = margins.left
}

