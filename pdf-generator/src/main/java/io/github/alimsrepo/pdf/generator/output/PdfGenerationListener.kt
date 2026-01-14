package io.github.alimsrepo.pdf.generator.output


/**
 * Listener for PDF generation events
 */
interface PdfGenerationListener {
    /**
     * Called when PDF generation starts
     */
    fun onStart() {}

    /**
     * Called to report progress
     * @param currentPage Current page being processed
     * @param totalPages Total number of pages
     */
    fun onProgress(currentPage: Int, totalPages: Int) {}

    /**
     * Called when PDF generation completes successfully
     */
    fun onSuccess(result: PdfResult) {}

    /**
     * Called when PDF generation fails
     */
    fun onFailure(error: PdfError) {}
}