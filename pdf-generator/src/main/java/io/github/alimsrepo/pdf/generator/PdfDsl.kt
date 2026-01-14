package io.github.alimsrepo.pdf.generator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.view.View
import io.github.alimsrepo.pdf.generator.config.CustomPageSize
import io.github.alimsrepo.pdf.generator.config.PageMargins
import io.github.alimsrepo.pdf.generator.config.PageOrientation
import io.github.alimsrepo.pdf.generator.config.PageSize
import io.github.alimsrepo.pdf.generator.config.PdfMetadata
import io.github.alimsrepo.pdf.generator.config.Watermark
import io.github.alimsrepo.pdf.generator.content.CheckboxItem
import io.github.alimsrepo.pdf.generator.content.PdfElement
import io.github.alimsrepo.pdf.generator.content.TableCell
import io.github.alimsrepo.pdf.generator.content.TableRow
import io.github.alimsrepo.pdf.generator.content.TextAlign
import io.github.alimsrepo.pdf.generator.content.TextElement
import io.github.alimsrepo.pdf.generator.output.PdfOutput
import io.github.alimsrepo.pdf.generator.output.PdfResult
import java.io.File
import java.io.OutputStream

/**
 * Kotlin DSL for building PDFs
 *
 * Usage:
 * ```kotlin
 * pdf {
 *     pageSize(PageSize.A4)
 *     orientation(PageOrientation.PORTRAIT)
 *     margins(PageMargins.NORMAL)
 *
 *     title("Document Title")
 *     text("Some content here...")
 *
 *     spacer(20f)
 *     divider()
 *
 *     table {
 *         header("Name", "Age", "City")
 *         row("John", "25", "New York")
 *         row("Jane", "30", "London")
 *     }
 * }
 * ```
 */
fun pdf(block: PdfDsl.() -> Unit): PdfBuilder {
    return PdfDsl().apply(block).builder
}

/**
 * DSL class for PDF building
 */
class PdfDsl {

    internal val builder = PdfBuilder()

    // Page configuration
    fun pageSize(size: PageSize) = builder.setPageSize(size)
    fun customPageSize(size: CustomPageSize) = builder.setCustomPageSize(size)
    fun pageSizeMm(width: Float, height: Float) = builder.setPageSizeMm(width, height)
    fun pageSizeInches(width: Float, height: Float) = builder.setPageSizeInches(width, height)
    fun orientation(orientation: PageOrientation) = builder.setOrientation(orientation)
    fun margins(margins: PageMargins) = builder.setMargins(margins)
    fun margins(top: Float, bottom: Float, left: Float, right: Float) = builder.setMargins(top, bottom, left, right)
    fun marginsMm(top: Float, bottom: Float, left: Float, right: Float) = builder.setMarginsMm(top, bottom, left, right)
    fun backgroundColor(color: Int) = builder.setBackgroundColor(color)

    // Header/Footer
    fun header(
        left: String? = null,
        center: String? = null,
        right: String? = null,
        showPageNumber: Boolean = false,
        pageNumberFormat: String = "Page {page} of {total}"
    ) = builder.setHeader(
        leftText = left,
        centerText = center,
        rightText = right,
        showPageNumber = showPageNumber,
        pageNumberFormat = pageNumberFormat
    )

    fun footer(
        left: String? = null,
        center: String? = null,
        right: String? = null,
        showPageNumber: Boolean = false,
        pageNumberFormat: String = "Page {page} of {total}"
    ) = builder.setFooter(
        leftText = left,
        centerText = center,
        rightText = right,
        showPageNumber = showPageNumber,
        pageNumberFormat = pageNumberFormat
    )

    // Content
    fun text(
        content: String,
        size: Float = 12f,
        color: Int = 0xFF000000.toInt(),
        typeface: Typeface = Typeface.DEFAULT,
        align: TextAlign = TextAlign.LEFT
    ) = builder.addText(content, size, color, typeface, align)

    fun title(content: String, size: Float = 24f, align: TextAlign = TextAlign.LEFT) =
        builder.addTitle(content, size, alignment = align)

    fun heading(content: String, size: Float = 18f, align: TextAlign = TextAlign.LEFT) =
        builder.addHeading(content, size, alignment = align)

    fun subheading(content: String, size: Float = 14f, align: TextAlign = TextAlign.LEFT) =
        builder.addSubheading(content, size, alignment = align)

    fun image(bitmap: Bitmap, width: Float? = null, height: Float? = null, align: TextAlign = TextAlign.CENTER) =
        builder.addImage(bitmap, width, height, align)

    fun spacer(height: Float) = builder.addSpacer(height)

    fun divider(thickness: Float = 1f, color: Int = 0xFF000000.toInt()) =
        builder.addDivider(thickness, color)

    fun dashedDivider(thickness: Float = 1f, color: Int = 0xFF000000.toInt()) =
        builder.addDashedDivider(thickness, color)

    fun bulletList(vararg items: String) = builder.addBulletList(items.toList())

    fun numberedList(vararg items: String) = builder.addNumberedList(items.toList())

    fun pageBreak() = builder.addPageBreak()

    // Watermark
    fun watermark(watermark: Watermark) = builder.setWatermark(watermark)
    fun textWatermark(text: String, textSize: Float = 48f, textColor: Int = 0x33000000, rotation: Float = -45f) =
        builder.setTextWatermark(text, textSize, textColor, rotation)
    fun draftWatermark() = builder.setWatermark(Watermark.draft())
    fun confidentialWatermark() = builder.setWatermark(Watermark.confidential())

    // Metadata
    fun metadata(block: PdfMetadata.Builder.() -> Unit) = builder.setMetadata(block)

    // Box elements
    fun box(elements: List<PdfElement>, padding: Float = 12f, backgroundColor: Int? = null) =
        builder.addBox(elements, padding, backgroundColor)
    fun infoBox(vararg elements: PdfElement) = builder.addInfoBox(*elements)
    fun warningBox(vararg elements: PdfElement) = builder.addWarningBox(*elements)
    fun errorBox(vararg elements: PdfElement) = builder.addErrorBox(*elements)
    fun successBox(vararg elements: PdfElement) = builder.addSuccessBox(*elements)

    // Checkbox elements
    fun checkbox(label: String, isChecked: Boolean = false) = builder.addCheckbox(label, isChecked)
    fun checkboxList(items: List<CheckboxItem>) = builder.addCheckboxList(items)
    fun checkboxList(vararg labels: String) = builder.addCheckboxList(*labels)

    // QR Code elements
    fun qrCode(data: String, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCode(data, size, align)
    fun qrCodeUrl(url: String, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCodeUrl(url, size, align)
    fun qrCodeEmail(email: String, subject: String? = null, body: String? = null, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCodeEmail(email, subject, body, size, align)
    fun qrCodePhone(phone: String, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCodePhone(phone, size, align)
    fun qrCodeSms(phone: String, message: String? = null, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCodeSms(phone, message, size, align)
    fun qrCodeWifi(ssid: String, password: String? = null, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCodeWifi(ssid, password, size = size, alignment = align)
    fun qrCodeVCard(
        firstName: String,
        lastName: String? = null,
        phone: String? = null,
        email: String? = null,
        organization: String? = null,
        size: Float = 150f,
        align: TextAlign = TextAlign.CENTER
    ) = builder.addQRCodeVCard(firstName, lastName, phone, email, organization, size = size, alignment = align)
    fun qrCodeLocation(latitude: Double, longitude: Double, size: Float = 150f, align: TextAlign = TextAlign.CENTER) =
        builder.addQRCodeLocation(latitude, longitude, size, align)

    // Table DSL
    fun table(block: TableDsl.() -> Unit) {
        val tableDsl = TableDsl().apply(block)
        builder.addTable(tableDsl.rows)
    }

    // Nested element DSL
    fun element(element: PdfElement) = builder.addElement(element)
}

/**
 * DSL for building tables
 */
class TableDsl {
    internal val rows = mutableListOf<TableRow>()

    fun header(vararg cells: String) {
        rows.add(TableRow(
            cells = cells.map { TableCell(content = it, typeface = Typeface.DEFAULT_BOLD) },
            isHeader = true
        ))
    }

    fun row(vararg cells: String) {
        rows.add(TableRow(
            cells = cells.map { TableCell(content = it) },
            isHeader = false
        ))
    }

    fun row(cells: List<TableCell>) {
        rows.add(TableRow(cells = cells, isHeader = false))
    }

    fun headerCells(vararg cells: TableCell) {
        rows.add(TableRow(cells = cells.toList(), isHeader = true))
    }
}

// ==================== Extension Functions ====================

/**
 * Extension to save PDF to file easily
 */
fun PdfBuilder.saveToFile(file: File): Result<File> {
    return buildSync(PdfOutput.ToFile(file)).map { result ->
        (result as PdfResult.FileResult).file
    }
}

/**
 * Extension to save PDF to path
 */
fun PdfBuilder.saveToPath(directoryPath: String, fileName: String): Result<File> {
    return buildSync(PdfOutput.ToPath(directoryPath, fileName)).map { result ->
        (result as PdfResult.FileResult).file
    }
}

/**
 * Extension to get PDF as byte array
 */
fun PdfBuilder.toByteArray(): Result<ByteArray> {
    return buildSync(PdfOutput.ToByteArray).map { result ->
        (result as PdfResult.ByteArrayResult).bytes
    }
}

/**
 * Extension to write PDF to OutputStream
 */
fun PdfBuilder.toOutputStream(outputStream: OutputStream): Result<Long> {
    return buildSync(PdfOutput.ToOutputStream(outputStream)).map { result ->
        (result as PdfResult.StreamResult).bytesWritten
    }
}

// ==================== View PDF DSL ====================

/**
 * DSL for building PDFs from Views
 */
fun viewPdf(context: Context, block: ViewPdfDsl.() -> Unit): ViewPdfBuilder {
    return ViewPdfDsl(context).apply(block).builder
}

class ViewPdfDsl(context: Context) {
    internal val builder = ViewPdfBuilder(context)

    fun pageSize(size: PageSize) = builder.setPageSize(size)
    fun customPageSize(size: CustomPageSize) = builder.setCustomPageSize(size)
    fun orientation(orientation: PageOrientation) = builder.setOrientation(orientation)
    fun margins(margins: PageMargins) = builder.setMargins(margins)
    fun margins(top: Float, bottom: Float, left: Float, right: Float) =
        builder.setMargins(PageMargins(top, bottom, left, right))
    fun marginsMm(top: Float, bottom: Float, left: Float, right: Float) =
        builder.setMargins(PageMargins.fromMm(top, bottom, left, right))
    fun backgroundColor(color: Int) = builder.setBackgroundColor(color)

    fun view(view: View) = builder.addView(view)
    fun views(views: List<View>) = builder.addViews(views)
}

