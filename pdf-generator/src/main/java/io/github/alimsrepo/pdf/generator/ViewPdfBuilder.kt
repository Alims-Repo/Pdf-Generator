package io.github.alimsrepo.pdf.generator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import io.github.alimsrepo.pdf.generator.config.CustomPageSize
import io.github.alimsrepo.pdf.generator.config.PageConfig
import io.github.alimsrepo.pdf.generator.config.PageMargins
import io.github.alimsrepo.pdf.generator.config.PageOrientation
import io.github.alimsrepo.pdf.generator.config.PageSize
import io.github.alimsrepo.pdf.generator.output.PdfError
import io.github.alimsrepo.pdf.generator.output.PdfGenerationListener
import io.github.alimsrepo.pdf.generator.output.PdfOutput
import io.github.alimsrepo.pdf.generator.output.PdfResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Builder for creating PDFs from Android Views
 * Supports automatic pagination when content exceeds page height
 */
class ViewPdfBuilder(private val context: Context) {

    private var pageSize: PageSize = PageSize.A4
    private var customPageSize: CustomPageSize? = null
    private var orientation: PageOrientation = PageOrientation.PORTRAIT
    private var margins: PageMargins = PageMargins.NORMAL
    private var backgroundColor: Int = 0xFFFFFFFF.toInt()

    private val views = mutableListOf<View>()

    /**
     * Set the page size
     */
    fun setPageSize(size: PageSize): ViewPdfBuilder {
        this.pageSize = size
        this.customPageSize = null
        return this
    }

    /**
     * Set custom page size
     */
    fun setCustomPageSize(customSize: CustomPageSize): ViewPdfBuilder {
        this.customPageSize = customSize
        return this
    }

    /**
     * Set page orientation
     */
    fun setOrientation(orientation: PageOrientation): ViewPdfBuilder {
        this.orientation = orientation
        return this
    }

    /**
     * Set page margins
     */
    fun setMargins(margins: PageMargins): ViewPdfBuilder {
        this.margins = margins
        return this
    }

    /**
     * Set background color
     */
    fun setBackgroundColor(color: Int): ViewPdfBuilder {
        this.backgroundColor = color
        return this
    }

    /**
     * Add a view to be rendered as a page
     */
    fun addView(view: View): ViewPdfBuilder {
        views.add(view)
        return this
    }

    /**
     * Add multiple views, each becoming a page
     */
    fun addViews(views: List<View>): ViewPdfBuilder {
        this.views.addAll(views)
        return this
    }

    /**
     * Add a view from layout resource
     */
    fun addViewFromLayout(@LayoutRes layoutRes: Int): ViewPdfBuilder {
        val view = LayoutInflater.from(context).inflate(layoutRes, null)
        views.add(view)
        return this
    }

    /**
     * Add a view from layout resource with data binding callback
     */
    fun addViewFromLayout(@LayoutRes layoutRes: Int, bind: (View) -> Unit): ViewPdfBuilder {
        val view = LayoutInflater.from(context).inflate(layoutRes, null)
        bind(view)
        views.add(view)
        return this
    }

    /**
     * Get page configuration
     */
    private fun getPageConfig(): PageConfig {
        return PageConfig(
            pageSize = pageSize,
            customPageSize = customPageSize,
            orientation = orientation,
            margins = margins,
            backgroundColor = backgroundColor
        )
    }

    /**
     * Build the PDF
     */
    fun build(output: PdfOutput, listener: PdfGenerationListener? = null) {
        try {
            listener?.onStart()

            val pageConfig = getPageConfig()
            val pageWidth = pageConfig.pageWidth.toInt()
            val pageHeight = pageConfig.pageHeight.toInt()
            val contentWidth = pageConfig.contentWidth.toInt()
            val contentHeight = pageConfig.contentHeight.toInt()

            val document = PdfDocument()
            var pageNumber = 1

            for (view in views) {
                listener?.onProgress(pageNumber, views.size)

                // Measure view with content width
                val widthSpec = View.MeasureSpec.makeMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY)
                val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                view.measure(widthSpec, heightSpec)

                val viewHeight = view.measuredHeight
                val viewWidth = view.measuredWidth

                // Check if view fits on one page or needs splitting
                if (viewHeight <= contentHeight) {
                    // Single page
                    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    val page = document.startPage(pageInfo)

                    view.layout(0, 0, viewWidth, viewHeight)

                    val canvas = page.canvas
                    canvas.save()
                    canvas.translate(pageConfig.contentStartX, pageConfig.contentStartY)
                    view.draw(canvas)
                    canvas.restore()

                    document.finishPage(page)
                    pageNumber++
                } else {
                    // Multi-page: render view to bitmap and split
                    view.layout(0, 0, viewWidth, viewHeight)

                    val bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
                    val bitmapCanvas = Canvas(bitmap)
                    view.draw(bitmapCanvas)

                    var yOffset = 0
                    while (yOffset < viewHeight) {
                        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        val page = document.startPage(pageInfo)
                        val canvas = page.canvas

                        val remainingHeight = viewHeight - yOffset
                        val drawHeight = minOf(contentHeight, remainingHeight)

                        // Create a bitmap slice for this page
                        val sliceBitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            yOffset,
                            viewWidth,
                            drawHeight
                        )

                        canvas.drawBitmap(
                            sliceBitmap,
                            pageConfig.contentStartX,
                            pageConfig.contentStartY,
                            null
                        )

                        sliceBitmap.recycle()

                        document.finishPage(page)
                        pageNumber++
                        yOffset += contentHeight
                    }

                    bitmap.recycle()
                }
            }

            // Write output
            val result = when (output) {
                is PdfOutput.ToFile -> {
                    FileOutputStream(output.file).use { fos ->
                        document.writeTo(fos)
                    }
                    document.close()
                    PdfResult.FileResult(output.file)
                }

                is PdfOutput.ToPath -> {
                    val dir = File(output.directoryPath)
                    if (!dir.exists()) dir.mkdirs()
                    val file = File(dir, "${output.fileName}.pdf")
                    FileOutputStream(file).use { fos ->
                        document.writeTo(fos)
                    }
                    document.close()
                    PdfResult.FileResult(file)
                }

                is PdfOutput.ToByteArray -> {
                    val baos = ByteArrayOutputStream()
                    document.writeTo(baos)
                    document.close()
                    PdfResult.ByteArrayResult(baos.toByteArray())
                }
            }

            listener?.onSuccess(result)

        } catch (e: Exception) {
            listener?.onFailure(PdfError("PDF generation failed: ${e.message}", e))
        }
    }

    /**
     * Build synchronously
     */
    fun buildSync(output: PdfOutput): Result<PdfResult> {
        return try {
            var syncResult: PdfResult? = null
            var syncError: PdfError? = null

            build(output, object : PdfGenerationListener {
                override fun onSuccess(result: PdfResult) {
                    syncResult = result
                }

                override fun onFailure(error: PdfError) {
                    syncError = error
                }
            })

            when {
                syncResult != null -> Result.success(syncResult!!)
                syncError != null -> Result.failure(Exception(syncError!!.message, syncError!!.exception))
                else -> Result.failure(Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        fun with(context: Context): ViewPdfBuilder = ViewPdfBuilder(context)
    }
}

