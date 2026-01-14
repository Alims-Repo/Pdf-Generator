package io.github.alimsrepo.pdf.generator.renderer

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import io.github.alimsrepo.pdf.generator.config.HeaderFooterContent
import io.github.alimsrepo.pdf.generator.config.PageConfig
import io.github.alimsrepo.pdf.generator.content.DividerElement
import io.github.alimsrepo.pdf.generator.content.ImageElement
import io.github.alimsrepo.pdf.generator.content.ListElement
import io.github.alimsrepo.pdf.generator.content.PageBreakElement
import io.github.alimsrepo.pdf.generator.content.PdfElement
import io.github.alimsrepo.pdf.generator.content.SpacerElement
import io.github.alimsrepo.pdf.generator.content.TableCell
import io.github.alimsrepo.pdf.generator.content.TableElement
import io.github.alimsrepo.pdf.generator.content.TableRow
import io.github.alimsrepo.pdf.generator.content.TextAlign
import io.github.alimsrepo.pdf.generator.content.TextElement
import io.github.alimsrepo.pdf.generator.content.NumberedListChunk

/**
 * Renders PDF elements to a Canvas
 */
class ElementRenderer(
    private val pageConfig: PageConfig
) {

    private val textPaint = Paint().apply {
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private val fillPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    /**
     * Render an element to the canvas at the specified position
     * @return The height consumed by this element
     */
    fun render(canvas: Canvas, element: PdfElement, x: Float, y: Float, availableWidth: Float): Float {
        return when (element) {
            is TextElement -> renderText(canvas, element, x, y, availableWidth)
            is ImageElement -> renderImage(canvas, element, x, y, availableWidth)
            is SpacerElement -> element.height
            is DividerElement -> renderDivider(canvas, element, x, y, availableWidth)
            is TableElement -> renderTable(canvas, element, x, y, availableWidth)
            is ListElement -> renderList(canvas, element, x, y, availableWidth)
            is NumberedListChunk -> renderNumberedListChunk(canvas, element, x, y, availableWidth)
            is PageBreakElement -> 0f
        }
    }

    private fun renderText(canvas: Canvas, element: TextElement, x: Float, y: Float, availableWidth: Float): Float {
        textPaint.apply {
            textSize = element.textSize
            color = element.textColor
            typeface = element.typeface
        }

        val effectiveWidth = availableWidth - element.indent
        val lines = TextElement.wrapText(element.text, effectiveWidth, textPaint)
        val lineHeight = element.textSize * element.lineSpacing

        var currentY = y + element.textSize
        val startX = x + element.indent

        val linesToDraw = lines.take(element.maxLines)

        for (line in linesToDraw) {
            val lineX = when (element.alignment) {
                TextAlign.LEFT -> startX
                TextAlign.CENTER -> startX + (effectiveWidth - textPaint.measureText(line)) / 2
                TextAlign.RIGHT -> startX + effectiveWidth - textPaint.measureText(line)
                TextAlign.JUSTIFY -> startX // TODO: Implement justify
            }

            canvas.drawText(line, lineX, currentY, textPaint)
            currentY += lineHeight
        }

        return (linesToDraw.size * lineHeight) + element.paragraphSpacing
    }

    private fun renderImage(canvas: Canvas, element: ImageElement, x: Float, y: Float, availableWidth: Float): Float {
        val bitmap = element.bitmap
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()

        val targetWidth = element.width ?: availableWidth
        val targetHeight = element.height ?: (targetWidth * aspectRatio)

        val imageX = when (element.alignment) {
            TextAlign.LEFT -> x
            TextAlign.CENTER -> x + (availableWidth - targetWidth) / 2
            TextAlign.RIGHT -> x + availableWidth - targetWidth
            TextAlign.JUSTIFY -> x
        }

        val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
        val dstRect = RectF(imageX, y, imageX + targetWidth, y + targetHeight)

        canvas.drawBitmap(bitmap, srcRect, dstRect, null)

        return targetHeight + element.spacingAfter
    }

    private fun renderDivider(canvas: Canvas, element: DividerElement, x: Float, y: Float, availableWidth: Float): Float {
        linePaint.apply {
            color = element.color
            strokeWidth = element.thickness
            pathEffect = if (element.dashWidth > 0) {
                DashPathEffect(floatArrayOf(element.dashWidth, element.dashGap), 0f)
            } else null
        }

        val lineY = y + element.marginTop + (element.thickness / 2)
        canvas.drawLine(x, lineY, x + availableWidth, lineY, linePaint)

        return element.thickness + element.marginTop + element.marginBottom
    }

    private fun renderTable(canvas: Canvas, element: TableElement, x: Float, y: Float, availableWidth: Float): Float {
        val colWidths = element.calculateColumnWidths(availableWidth)
        var currentY = y

        linePaint.apply {
            color = element.borderColor
            strokeWidth = element.borderWidth
            pathEffect = null
        }

        element.rows.forEachIndexed { rowIndex, row ->
            var currentX = x
            val rowHeight = measureRowHeight(row, colWidths)

            // Draw row background
            val bgColor = when {
                row.isHeader -> element.headerBackgroundColor
                element.alternateRowColor != null && rowIndex % 2 == 1 -> element.alternateRowColor
                else -> null
            }

            if (bgColor != null) {
                fillPaint.color = bgColor
                canvas.drawRect(x, currentY, x + availableWidth, currentY + rowHeight, fillPaint)
            }

            // Draw cells
            row.cells.forEachIndexed { cellIndex, cell ->
                if (cellIndex < colWidths.size) {
                    val cellWidth = colWidths[cellIndex]

                    // Cell background
                    cell.backgroundColor?.let { bgc ->
                        fillPaint.color = bgc
                        canvas.drawRect(currentX, currentY, currentX + cellWidth, currentY + rowHeight, fillPaint)
                    }

                    // Cell border
                    canvas.drawRect(currentX, currentY, currentX + cellWidth, currentY + rowHeight, linePaint)

                    // Cell text
                    renderCellText(canvas, cell, currentX, currentY, cellWidth, rowHeight)

                    currentX += cellWidth
                }
            }

            currentY += rowHeight
        }

        return (currentY - y) + element.spacingAfter
    }

    private fun renderCellText(canvas: Canvas, cell: TableCell, x: Float, y: Float, width: Float, height: Float) {
        textPaint.apply {
            textSize = cell.textSize
            color = cell.textColor
            typeface = cell.typeface
        }

        val cellContentWidth = width - (cell.padding * 2)
        val lines = TextElement.wrapText(cell.content, cellContentWidth, textPaint)
        val lineHeight = cell.textSize * 1.2f
        val totalTextHeight = lines.size * lineHeight

        var textY = y + cell.padding + cell.textSize + ((height - cell.padding * 2 - totalTextHeight) / 2)

        for (line in lines) {
            val textX = when (cell.alignment) {
                TextAlign.LEFT -> x + cell.padding
                TextAlign.CENTER -> x + cell.padding + (cellContentWidth - textPaint.measureText(line)) / 2
                TextAlign.RIGHT -> x + width - cell.padding - textPaint.measureText(line)
                TextAlign.JUSTIFY -> x + cell.padding
            }

            canvas.drawText(line, textX, textY, textPaint)
            textY += lineHeight
        }
    }

    private fun measureRowHeight(row: TableRow, colWidths: List<Float>): Float {
        var maxHeight = row.minHeight.coerceAtLeast(24f)

        row.cells.forEachIndexed { index, cell ->
            if (index < colWidths.size) {
                textPaint.textSize = cell.textSize
                textPaint.typeface = cell.typeface

                val cellWidth = colWidths[index] - (cell.padding * 2)
                val lines = TextElement.wrapText(cell.content, cellWidth, textPaint)
                val cellHeight = (lines.size * cell.textSize * 1.2f) + (cell.padding * 2)
                maxHeight = maxOf(maxHeight, cellHeight)
            }
        }

        return maxHeight
    }

    private fun renderList(canvas: Canvas, element: ListElement, x: Float, y: Float, availableWidth: Float): Float {
        textPaint.apply {
            textSize = element.textSize
            color = element.textColor
            typeface = element.typeface
        }

        var currentY = y
        val effectiveWidth = availableWidth - element.indent

        element.items.forEachIndexed { index, item ->
            val bullet = if (element.isNumbered) "${index + 1}." else element.bulletChar

            // Draw bullet
            canvas.drawText(bullet, x, currentY + element.textSize, textPaint)

            // Draw item text
            val lines = TextElement.wrapText(item, effectiveWidth, textPaint)
            var lineY = currentY + element.textSize

            for (line in lines) {
                canvas.drawText(line, x + element.indent, lineY, textPaint)
                lineY += element.textSize * 1.2f
            }

            currentY += (lines.size * element.textSize * 1.2f) + element.itemSpacing
        }

        return (currentY - y) + element.spacingAfter
    }

    private fun renderNumberedListChunk(canvas: Canvas, element: NumberedListChunk, x: Float, y: Float, availableWidth: Float): Float {
        textPaint.apply {
            textSize = element.textSize
            color = element.textColor
            typeface = element.typeface
        }

        var currentY = y
        val effectiveWidth = availableWidth - element.indent

        element.items.forEachIndexed { index, item ->
            val number = "${element.startNumber + index}."

            // Draw number
            canvas.drawText(number, x, currentY + element.textSize, textPaint)

            // Draw item text
            val lines = TextElement.wrapText(item, effectiveWidth, textPaint)
            var lineY = currentY + element.textSize

            for (line in lines) {
                canvas.drawText(line, x + element.indent, lineY, textPaint)
                lineY += element.textSize * 1.2f
            }

            currentY += (lines.size * element.textSize * 1.2f) + element.itemSpacing
        }

        return (currentY - y) + element.spacingAfter
    }

    /**
     * Render page header
     */
    fun renderHeader(canvas: Canvas, content: HeaderFooterContent, pageNumber: Int, totalPages: Int) {
        val y = pageConfig.margins.top
        renderHeaderFooterContent(canvas, content, y, pageNumber, totalPages)
    }

    /**
     * Render page footer
     */
    fun renderFooter(canvas: Canvas, content: HeaderFooterContent, pageNumber: Int, totalPages: Int) {
        val y = pageConfig.pageHeight - pageConfig.margins.bottom - content.textSize
        renderHeaderFooterContent(canvas, content, y, pageNumber, totalPages)
    }

    private fun renderHeaderFooterContent(canvas: Canvas, content: HeaderFooterContent, y: Float, pageNumber: Int, totalPages: Int) {
        textPaint.apply {
            textSize = content.textSize
            color = content.textColor
            typeface = content.typeface
        }

        val contentWidth = pageConfig.contentWidth
        val startX = pageConfig.margins.left

        // Left text
        content.leftText?.let { text ->
            val processedText = processPageNumber(text, pageNumber, totalPages)
            canvas.drawText(processedText, startX, y, textPaint)
        }

        // Center text
        content.centerText?.let { text ->
            val processedText = processPageNumber(text, pageNumber, totalPages)
            val textWidth = textPaint.measureText(processedText)
            canvas.drawText(processedText, startX + (contentWidth - textWidth) / 2, y, textPaint)
        }

        // Right text
        content.rightText?.let { text ->
            val processedText = processPageNumber(text, pageNumber, totalPages)
            val textWidth = textPaint.measureText(processedText)
            canvas.drawText(processedText, startX + contentWidth - textWidth, y, textPaint)
        }

        // Page number (if enabled and no custom placement)
        if (content.showPageNumber && content.leftText == null && content.centerText == null && content.rightText == null) {
            val pageText = processPageNumber(content.pageNumberFormat, pageNumber, totalPages)
            val textWidth = textPaint.measureText(pageText)
            canvas.drawText(pageText, startX + (contentWidth - textWidth) / 2, y, textPaint)
        }
    }

    private fun processPageNumber(text: String, pageNumber: Int, totalPages: Int): String {
        return text
            .replace("{page}", pageNumber.toString())
            .replace("{total}", totalPages.toString())
    }
}

