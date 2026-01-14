package io.github.alimsrepo.pdf.generator.output

/**
 * Error during PDF generation
 */
sealed class PdfError(
    open val message: String,
    open val exception: Throwable? = null
) {
    /**
     * Generic PDF generation error
     */
    data class GenerationError(
        override val message: String,
        override val exception: Throwable? = null
    ) : PdfError(message, exception)

    /**
     * Error writing to file/output
     */
    data class IoError(
        override val message: String,
        override val exception: Throwable? = null
    ) : PdfError(message, exception)

    /**
     * Error rendering a specific element
     */
    data class RenderError(
        val elementType: String,
        override val message: String,
        override val exception: Throwable? = null
    ) : PdfError(message, exception)

    /**
     * Error with invalid configuration
     */
    data class ConfigurationError(
        override val message: String,
        override val exception: Throwable? = null
    ) : PdfError(message, exception)

    /**
     * Error with page layout
     */
    data class LayoutError(
        override val message: String,
        override val exception: Throwable? = null
    ) : PdfError(message, exception)

    companion object {
        /**
         * Create a generic error from message
         */
        fun generic(message: String, exception: Throwable? = null): PdfError =
            GenerationError(message, exception)
    }
}
