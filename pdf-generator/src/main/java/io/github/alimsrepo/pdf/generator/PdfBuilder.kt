package io.github.alimsrepo.pdf.generator

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import io.github.alimsrepo.pdf.generator.config.CustomPageSize
import io.github.alimsrepo.pdf.generator.config.HeaderFooterContent
import io.github.alimsrepo.pdf.generator.config.PageConfig
import io.github.alimsrepo.pdf.generator.config.PageHeaderFooter
import io.github.alimsrepo.pdf.generator.config.PageMargins
import io.github.alimsrepo.pdf.generator.config.PageOrientation
import io.github.alimsrepo.pdf.generator.config.PageSize
import io.github.alimsrepo.pdf.generator.config.PdfMetadata
import io.github.alimsrepo.pdf.generator.config.Watermark
import io.github.alimsrepo.pdf.generator.content.BoxElement
import io.github.alimsrepo.pdf.generator.content.CheckboxElement
import io.github.alimsrepo.pdf.generator.content.CheckboxItem
import io.github.alimsrepo.pdf.generator.content.CheckboxListElement
import io.github.alimsrepo.pdf.generator.content.DividerElement
import io.github.alimsrepo.pdf.generator.content.ImageElement
import io.github.alimsrepo.pdf.generator.content.ImageScaleType
import io.github.alimsrepo.pdf.generator.content.ListElement
import io.github.alimsrepo.pdf.generator.content.PageBreakElement
import io.github.alimsrepo.pdf.generator.content.PdfElement
import io.github.alimsrepo.pdf.generator.content.SpacerElement
import io.github.alimsrepo.pdf.generator.content.TableCell
import io.github.alimsrepo.pdf.generator.content.TableElement
import io.github.alimsrepo.pdf.generator.content.TableRow
import io.github.alimsrepo.pdf.generator.content.TextAlign
import io.github.alimsrepo.pdf.generator.content.TextElement
import io.github.alimsrepo.pdf.generator.layout.PageLayoutEngine
import io.github.alimsrepo.pdf.generator.output.PdfError
import io.github.alimsrepo.pdf.generator.output.PdfGenerationListener
import io.github.alimsrepo.pdf.generator.output.PdfOutput
import io.github.alimsrepo.pdf.generator.output.PdfResult
import io.github.alimsrepo.pdf.generator.renderer.ElementRenderer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Main PDF Builder class with fluent API for creating customizable PDF documents
 *
 * Usage:
 * ```kotlin
 * PdfBuilder()
 *     .setPageSize(PageSize.A4)
 *     .setOrientation(PageOrientation.PORTRAIT)
 *     .setMargins(PageMargins.NORMAL)
 *     .addText("Hello World", textSize = 24f)
 *     .addText("This is a paragraph of text that will wrap automatically.")
 *     .addSpacer(20f)
 *     .addDivider()
 *     .build(PdfOutput.ToFile(outputFile), listener)
 * ```
 */
class PdfBuilder {

    private var pageSize: PageSize = PageSize.A4
    private var customPageSize: CustomPageSize? = null
    private var orientation: PageOrientation = PageOrientation.PORTRAIT
    private var margins: PageMargins = PageMargins.NORMAL
    private var header: PageHeaderFooter = PageHeaderFooter()
    private var footer: PageHeaderFooter = PageHeaderFooter()
    private var backgroundColor: Int = 0xFFFFFFFF.toInt()
    private var watermark: Watermark? = null
    private var metadata: PdfMetadata = PdfMetadata.EMPTY

    private val elements = mutableListOf<PdfElement>()

    // ==================== Page Configuration ====================

    /**
     * Set the page size using standard sizes
     */
    fun setPageSize(size: PageSize): PdfBuilder {
        this.pageSize = size
        this.customPageSize = null
        return this
    }

    /**
     * Set a custom page size
     */
    fun setCustomPageSize(customSize: CustomPageSize): PdfBuilder {
        this.customPageSize = customSize
        return this
    }

    /**
     * Set custom page size in millimeters
     */
    fun setPageSizeMm(widthMm: Float, heightMm: Float): PdfBuilder {
        this.customPageSize = PageSize.customMm(widthMm, heightMm)
        return this
    }

    /**
     * Set custom page size in inches
     */
    fun setPageSizeInches(widthInches: Float, heightInches: Float): PdfBuilder {
        this.customPageSize = PageSize.customInches(widthInches, heightInches)
        return this
    }

    /**
     * Set page orientation
     */
    fun setOrientation(orientation: PageOrientation): PdfBuilder {
        this.orientation = orientation
        return this
    }

    /**
     * Set page margins using preset
     */
    fun setMargins(margins: PageMargins): PdfBuilder {
        this.margins = margins
        return this
    }

    /**
     * Set page margins with individual values in points
     */
    fun setMargins(top: Float, bottom: Float, left: Float, right: Float): PdfBuilder {
        this.margins = PageMargins(top, bottom, left, right)
        return this
    }

    /**
     * Set page margins in millimeters
     */
    fun setMarginsMm(top: Float, bottom: Float, left: Float, right: Float): PdfBuilder {
        this.margins = PageMargins.fromMm(top, bottom, left, right)
        return this
    }

    /**
     * Set uniform margins on all sides
     */
    fun setUniformMargins(margin: Float): PdfBuilder {
        this.margins = PageMargins.uniform(margin)
        return this
    }

    /**
     * Set page background color
     */
    fun setBackgroundColor(color: Int): PdfBuilder {
        this.backgroundColor = color
        return this
    }

    /**
     * Configure page header
     */
    fun setHeader(
        leftText: String? = null,
        centerText: String? = null,
        rightText: String? = null,
        showPageNumber: Boolean = false,
        pageNumberFormat: String = "Page {page} of {total}",
        textSize: Float = 10f,
        textColor: Int = 0xFF000000.toInt(),
        height: Float = 40f
    ): PdfBuilder {
        this.header = PageHeaderFooter(
            enabled = true,
            height = height,
            content = HeaderFooterContent(
                leftText = leftText,
                centerText = centerText,
                rightText = rightText,
                showPageNumber = showPageNumber,
                pageNumberFormat = pageNumberFormat,
                textSize = textSize,
                textColor = textColor
            )
        )
        return this
    }

    /**
     * Configure page footer
     */
    fun setFooter(
        leftText: String? = null,
        centerText: String? = null,
        rightText: String? = null,
        showPageNumber: Boolean = false,
        pageNumberFormat: String = "Page {page} of {total}",
        textSize: Float = 10f,
        textColor: Int = 0xFF000000.toInt(),
        height: Float = 40f
    ): PdfBuilder {
        this.footer = PageHeaderFooter(
            enabled = true,
            height = height,
            content = HeaderFooterContent(
                leftText = leftText,
                centerText = centerText,
                rightText = rightText,
                showPageNumber = showPageNumber,
                pageNumberFormat = pageNumberFormat,
                textSize = textSize,
                textColor = textColor
            )
        )
        return this
    }

    /**
     * Set a watermark for all pages
     */
    fun setWatermark(watermark: Watermark): PdfBuilder {
        this.watermark = watermark
        return this
    }

    /**
     * Set a text watermark
     */
    fun setTextWatermark(
        text: String,
        textSize: Float = 48f,
        textColor: Int = 0x33000000,
        rotation: Float = -45f
    ): PdfBuilder {
        this.watermark = Watermark.text(text, textSize, textColor, rotation)
        return this
    }

    /**
     * Set document metadata
     */
    fun setMetadata(metadata: PdfMetadata): PdfBuilder {
        this.metadata = metadata
        return this
    }

    /**
     * Set document metadata using builder
     */
    fun setMetadata(block: PdfMetadata.Builder.() -> Unit): PdfBuilder {
        this.metadata = PdfMetadata.Builder().apply(block).build()
        return this
    }

    // ==================== Content Addition ====================

    /**
     * Add a text element
     */
    fun addText(
        text: String,
        textSize: Float = 12f,
        textColor: Int = 0xFF000000.toInt(),
        typeface: Typeface = Typeface.DEFAULT,
        alignment: TextAlign = TextAlign.LEFT,
        lineSpacing: Float = 1.2f,
        paragraphSpacing: Float = 8f,
        indent: Float = 0f
    ): PdfBuilder {
        elements.add(
            TextElement(
                text = text,
                textSize = textSize,
                textColor = textColor,
                typeface = typeface,
                alignment = alignment,
                lineSpacing = lineSpacing,
                paragraphSpacing = paragraphSpacing,
                indent = indent
            )
        )
        return this
    }

    /**
     * Add a title (large, bold text)
     */
    fun addTitle(
        text: String,
        textSize: Float = 24f,
        textColor: Int = 0xFF000000.toInt(),
        alignment: TextAlign = TextAlign.LEFT
    ): PdfBuilder {
        return addText(
            text = text,
            textSize = textSize,
            textColor = textColor,
            typeface = Typeface.DEFAULT_BOLD,
            alignment = alignment,
            paragraphSpacing = 16f
        )
    }

    /**
     * Add a heading
     */
    fun addHeading(
        text: String,
        textSize: Float = 18f,
        textColor: Int = 0xFF000000.toInt(),
        alignment: TextAlign = TextAlign.LEFT
    ): PdfBuilder {
        return addText(
            text = text,
            textSize = textSize,
            textColor = textColor,
            typeface = Typeface.DEFAULT_BOLD,
            alignment = alignment,
            paragraphSpacing = 12f
        )
    }

    /**
     * Add a subheading
     */
    fun addSubheading(
        text: String,
        textSize: Float = 14f,
        textColor: Int = 0xFF000000.toInt(),
        alignment: TextAlign = TextAlign.LEFT
    ): PdfBuilder {
        return addText(
            text = text,
            textSize = textSize,
            textColor = textColor,
            typeface = Typeface.DEFAULT_BOLD,
            alignment = alignment,
            paragraphSpacing = 10f
        )
    }

    /**
     * Add an image from bitmap
     */
    fun addImage(
        bitmap: Bitmap,
        width: Float? = null,
        height: Float? = null,
        alignment: TextAlign = TextAlign.CENTER,
        scaleType: ImageScaleType = ImageScaleType.FIT,
        spacingAfter: Float = 8f
    ): PdfBuilder {
        elements.add(
            ImageElement(
                bitmap = bitmap,
                width = width,
                height = height,
                alignment = alignment,
                scaleType = scaleType,
                spacingAfter = spacingAfter
            )
        )
        return this
    }

    /**
     * Add vertical spacing
     */
    fun addSpacer(height: Float): PdfBuilder {
        elements.add(SpacerElement(height))
        return this
    }

    /**
     * Add a horizontal divider line
     */
    fun addDivider(
        thickness: Float = 1f,
        color: Int = 0xFF000000.toInt(),
        marginTop: Float = 8f,
        marginBottom: Float = 8f,
        dashWidth: Float = 0f,
        dashGap: Float = 0f
    ): PdfBuilder {
        elements.add(
            DividerElement(
                thickness = thickness,
                color = color,
                marginTop = marginTop,
                marginBottom = marginBottom,
                dashWidth = dashWidth,
                dashGap = dashGap
            )
        )
        return this
    }

    /**
     * Add a dashed divider
     */
    fun addDashedDivider(
        thickness: Float = 1f,
        color: Int = 0xFF000000.toInt(),
        dashWidth: Float = 5f,
        dashGap: Float = 3f
    ): PdfBuilder {
        return addDivider(
            thickness = thickness,
            color = color,
            dashWidth = dashWidth,
            dashGap = dashGap
        )
    }

    /**
     * Add a table
     */
    fun addTable(
        rows: List<TableRow>,
        columnWidths: List<Float>? = null,
        borderWidth: Float = 0.5f,
        borderColor: Int = 0xFF000000.toInt(),
        headerBackgroundColor: Int = 0xFFEEEEEE.toInt(),
        alternateRowColor: Int? = null,
        spacingAfter: Float = 8f
    ): PdfBuilder {
        elements.add(
            TableElement(
                rows = rows,
                columnWidths = columnWidths,
                borderWidth = borderWidth,
                borderColor = borderColor,
                headerBackgroundColor = headerBackgroundColor,
                alternateRowColor = alternateRowColor,
                spacingAfter = spacingAfter
            )
        )
        return this
    }

    /**
     * Add a simple table from 2D string array
     */
    fun addSimpleTable(
        data: List<List<String>>,
        hasHeader: Boolean = true,
        textSize: Float = 11f,
        cellPadding: Float = 4f
    ): PdfBuilder {
        val rows = data.mapIndexed { index, rowData ->
            TableRow(
                cells = rowData.map { cellText ->
                    TableCell(
                        content = cellText,
                        textSize = textSize,
                        padding = cellPadding
                    )
                },
                isHeader = hasHeader && index == 0
            )
        }
        return addTable(rows)
    }

    /**
     * Add a bullet list
     */
    fun addBulletList(
        items: List<String>,
        textSize: Float = 12f,
        textColor: Int = 0xFF000000.toInt(),
        bulletChar: String = "•",
        indent: Float = 20f
    ): PdfBuilder {
        elements.add(
            ListElement(
                items = items,
                textSize = textSize,
                textColor = textColor,
                bulletChar = bulletChar,
                isNumbered = false,
                indent = indent
            )
        )
        return this
    }

    /**
     * Add a numbered list
     */
    fun addNumberedList(
        items: List<String>,
        textSize: Float = 12f,
        textColor: Int = 0xFF000000.toInt(),
        indent: Float = 20f
    ): PdfBuilder {
        elements.add(
            ListElement(
                items = items,
                textSize = textSize,
                textColor = textColor,
                isNumbered = true,
                indent = indent
            )
        )
        return this
    }

    /**
     * Force a page break
     */
    fun addPageBreak(): PdfBuilder {
        elements.add(PageBreakElement)
        return this
    }

    /**
     * Add a box/container element
     */
    fun addBox(
        elements: List<PdfElement>,
        padding: Float = 12f,
        backgroundColor: Int? = null,
        borderWidth: Float = 1f,
        borderColor: Int = 0xFF000000.toInt(),
        borderRadius: Float = 0f,
        spacingAfter: Float = 8f
    ): PdfBuilder {
        this.elements.add(
            BoxElement(
                elements = elements,
                padding = padding,
                backgroundColor = backgroundColor,
                borderWidth = borderWidth,
                borderColor = borderColor,
                borderRadius = borderRadius,
                spacingAfter = spacingAfter
            )
        )
        return this
    }

    /**
     * Add an info box
     */
    fun addInfoBox(vararg elements: PdfElement): PdfBuilder {
        this.elements.add(BoxElement.info(elements.toList()))
        return this
    }

    /**
     * Add a warning box
     */
    fun addWarningBox(vararg elements: PdfElement): PdfBuilder {
        this.elements.add(BoxElement.warning(elements.toList()))
        return this
    }

    /**
     * Add an error box
     */
    fun addErrorBox(vararg elements: PdfElement): PdfBuilder {
        this.elements.add(BoxElement.error(elements.toList()))
        return this
    }

    /**
     * Add a success box
     */
    fun addSuccessBox(vararg elements: PdfElement): PdfBuilder {
        this.elements.add(BoxElement.success(elements.toList()))
        return this
    }

    /**
     * Add a single checkbox
     */
    fun addCheckbox(
        label: String,
        isChecked: Boolean = false,
        textSize: Float = 12f,
        textColor: Int = 0xFF000000.toInt()
    ): PdfBuilder {
        elements.add(
            CheckboxElement(
                label = label,
                isChecked = isChecked,
                textSize = textSize,
                textColor = textColor
            )
        )
        return this
    }

    /**
     * Add a checkbox list
     */
    fun addCheckboxList(
        items: List<CheckboxItem>,
        textSize: Float = 12f,
        textColor: Int = 0xFF000000.toInt()
    ): PdfBuilder {
        elements.add(
            CheckboxListElement(
                items = items,
                textSize = textSize,
                textColor = textColor
            )
        )
        return this
    }

    /**
     * Add a checkbox list from strings (all unchecked)
     */
    fun addCheckboxList(
        vararg labels: String,
        textSize: Float = 12f
    ): PdfBuilder {
        elements.add(
            CheckboxListElement(
                items = labels.map { CheckboxItem(it, false) },
                textSize = textSize
            )
        )
        return this
    }

    /**
     * Add a custom element
     */
    fun addElement(element: PdfElement): PdfBuilder {
        elements.add(element)
        return this
    }

    /**
     * Add multiple elements at once
     */
    fun addElements(elements: List<PdfElement>): PdfBuilder {
        this.elements.addAll(elements)
        return this
    }

    // ==================== Building ====================

    /**
     * Get the current page configuration
     */
    fun getPageConfig(): PageConfig {
        return PageConfig(
            pageSize = pageSize,
            customPageSize = customPageSize,
            orientation = orientation,
            margins = margins,
            header = header,
            footer = footer,
            backgroundColor = backgroundColor
        )
    }

    /**
     * Build the PDF and save to specified output
     */
    fun build(output: PdfOutput, listener: PdfGenerationListener? = null) {
        var document: PdfDocument? = null
        try {
            listener?.onStart()

            val pageConfig = getPageConfig()
            val layoutEngine = PageLayoutEngine(pageConfig)
            val renderer = ElementRenderer(pageConfig)

            // Layout elements across pages
            val pages = layoutEngine.layoutElements(elements)
            val totalPages = pages.size

            // Create PDF document
            document = PdfDocument()

            for (pageContent in pages) {
                listener?.onProgress(pageContent.pageNumber, totalPages)

                // Create page
                val pageInfo = PdfDocument.PageInfo.Builder(
                    pageConfig.pageWidth.toInt(),
                    pageConfig.pageHeight.toInt(),
                    pageContent.pageNumber
                ).create()

                val page = document.startPage(pageInfo)
                val canvas = page.canvas

                // Draw background
                if (backgroundColor != 0xFFFFFFFF.toInt()) {
                    val bgPaint = Paint().apply {
                        color = backgroundColor
                        style = Paint.Style.FILL
                    }
                    canvas.drawRect(0f, 0f, pageConfig.pageWidth, pageConfig.pageHeight, bgPaint)
                }

                // Draw watermark (behind content)
                this@PdfBuilder.watermark?.let { wm -> renderer.renderWatermark(canvas, wm) }

                // Draw header
                if (header.enabled && header.content != null) {
                    renderer.renderHeader(canvas, header.content!!, pageContent.pageNumber, totalPages)
                }

                // Draw footer
                if (footer.enabled && footer.content != null) {
                    renderer.renderFooter(canvas, footer.content!!, pageContent.pageNumber, totalPages)
                }

                // Draw elements
                for (elementPos in pageContent.elements) {
                    renderer.render(
                        canvas,
                        elementPos.element,
                        elementPos.x,
                        elementPos.y,
                        elementPos.availableWidth
                    )
                }

                document.finishPage(page)
            }

            // Write output
            val result = when (output) {
                is PdfOutput.ToFile -> {
                    FileOutputStream(output.file).use { fos ->
                        document.writeTo(fos)
                    }
                    document.close()
                    document = null
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
                    document = null
                    PdfResult.FileResult(file)
                }

                is PdfOutput.ToByteArray -> {
                    val baos = ByteArrayOutputStream()
                    document.writeTo(baos)
                    document.close()
                    document = null
                    PdfResult.ByteArrayResult(baos.toByteArray())
                }

                is PdfOutput.ToOutputStream -> {
                    val baos = ByteArrayOutputStream()
                    document.writeTo(baos)
                    document.close()
                    document = null
                    val bytes = baos.toByteArray()
                    output.outputStream.write(bytes)
                    output.outputStream.flush()
                    PdfResult.StreamResult(bytes.size.toLong())
                }
            }

            listener?.onSuccess(result)

        } catch (e: java.io.IOException) {
            document?.close()
            listener?.onFailure(PdfError.IoError("Failed to write PDF: ${e.message}", e))
        } catch (e: Exception) {
            document?.close()
            listener?.onFailure(PdfError.GenerationError("PDF generation failed: ${e.message}", e))
        }
    }

    /**
     * Build the PDF synchronously and return the result
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

    /**
     * Clear all added elements
     */
    fun clear(): PdfBuilder {
        elements.clear()
        return this
    }

    /**
     * Get the number of elements added
     */
    fun getElementCount(): Int = elements.size

    /**
     * Calculate the estimated number of pages
     */
    fun estimatePageCount(): Int {
        val pageConfig = getPageConfig()
        val layoutEngine = PageLayoutEngine(pageConfig)
        return layoutEngine.calculatePageCount(elements)
    }

    companion object {
        /**
         * Create a new builder with default settings
         */
        fun create(): PdfBuilder = PdfBuilder()

        /**
         * Create a builder pre-configured for A4 portrait
         */
        fun a4Portrait(): PdfBuilder = PdfBuilder()
            .setPageSize(PageSize.A4)
            .setOrientation(PageOrientation.PORTRAIT)

        /**
         * Create a builder pre-configured for A4 landscape
         */
        fun a4Landscape(): PdfBuilder = PdfBuilder()
            .setPageSize(PageSize.A4)
            .setOrientation(PageOrientation.LANDSCAPE)

        /**
         * Create a builder pre-configured for A5 portrait
         */
        fun a5Portrait(): PdfBuilder = PdfBuilder()
            .setPageSize(PageSize.A5)
            .setOrientation(PageOrientation.PORTRAIT)

        /**
         * Create a builder pre-configured for Letter size
         */
        fun letter(): PdfBuilder = PdfBuilder()
            .setPageSize(PageSize.LETTER)
            .setOrientation(PageOrientation.PORTRAIT)
    }
}

