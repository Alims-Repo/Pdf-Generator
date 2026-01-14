package io.github.alimsrepo.pdf.generator.layout

import android.graphics.Paint
import io.github.alimsrepo.pdf.generator.config.PageConfig
import io.github.alimsrepo.pdf.generator.content.ListElement
import io.github.alimsrepo.pdf.generator.content.NumberedListChunk
import io.github.alimsrepo.pdf.generator.content.PageBreakElement
import io.github.alimsrepo.pdf.generator.content.PdfElement
import io.github.alimsrepo.pdf.generator.content.TableElement
import io.github.alimsrepo.pdf.generator.content.TableRow
import io.github.alimsrepo.pdf.generator.content.TextElement

/**
 * Engine responsible for laying out content across multiple pages
 */
class PageLayoutEngine(
    private val pageConfig: PageConfig
) {

    private val paint = Paint().apply { isAntiAlias = true }

    /**
     * Layout elements across pages, automatically creating new pages as needed
     * @return List of pages with positioned elements
     */
    fun layoutElements(elements: List<PdfElement>): List<PageContent> {
        val pages = mutableListOf<PageContent>()
        var currentPageElements = mutableListOf<ElementPosition>()
        var currentPageNumber = 1
        var currentY = pageConfig.contentStartY
        val contentWidth = pageConfig.contentWidth
        val startX = pageConfig.contentStartX
        val contentHeight = pageConfig.contentHeight
        val maxY = pageConfig.pageHeight - pageConfig.margins.bottom -
                   if (pageConfig.footer.enabled) pageConfig.footer.height else 0f

        for (element in elements) {
            // Handle explicit page break
            if (element is PageBreakElement) {
                pages.add(PageContent(currentPageNumber, currentPageElements))
                currentPageElements = mutableListOf()
                currentPageNumber++
                currentY = pageConfig.contentStartY
                continue
            }

            val elementHeight = element.measureHeight(contentWidth)
            val remainingHeight = maxY - currentY

            // Check if element fits on current page
            if (elementHeight <= remainingHeight) {
                // Element fits on current page
                currentPageElements.add(
                    ElementPosition(element, startX, currentY, contentWidth)
                )
                currentY += elementHeight
            } else if (element is TableElement) {
                // Table doesn't fit - split it across pages
                val tableChunks = splitTable(element, contentWidth, remainingHeight, contentHeight)

                for ((index, chunk) in tableChunks.withIndex()) {
                    if (index > 0) {
                        // Start new page for subsequent chunks
                        pages.add(PageContent(currentPageNumber, currentPageElements))
                        currentPageElements = mutableListOf()
                        currentPageNumber++
                        currentY = pageConfig.contentStartY
                    }

                    val chunkHeight = chunk.measureHeight(contentWidth)
                    currentPageElements.add(
                        ElementPosition(chunk, startX, currentY, contentWidth)
                    )
                    currentY += chunkHeight
                }
            } else if (element is ListElement && elementHeight > remainingHeight) {
                // List doesn't fit - split it
                val listChunks = splitList(element, contentWidth, remainingHeight, contentHeight)

                for ((index, chunk) in listChunks.withIndex()) {
                    if (index > 0) {
                        pages.add(PageContent(currentPageNumber, currentPageElements))
                        currentPageElements = mutableListOf()
                        currentPageNumber++
                        currentY = pageConfig.contentStartY
                    }

                    val chunkHeight = chunk.measureHeight(contentWidth)
                    currentPageElements.add(
                        ElementPosition(chunk, startX, currentY, contentWidth)
                    )
                    currentY += chunkHeight
                }
            } else {
                // Element doesn't fit but isn't splittable, just move to new page
                if (currentPageElements.isNotEmpty()) {
                    pages.add(PageContent(currentPageNumber, currentPageElements))
                    currentPageElements = mutableListOf()
                    currentPageNumber++
                    currentY = pageConfig.contentStartY
                }

                currentPageElements.add(
                    ElementPosition(element, startX, currentY, contentWidth)
                )
                currentY += elementHeight
            }
        }

        // Add the last page if it has content
        if (currentPageElements.isNotEmpty()) {
            pages.add(PageContent(currentPageNumber, currentPageElements))
        }

        // Ensure at least one page exists
        if (pages.isEmpty()) {
            pages.add(PageContent(1, emptyList()))
        }

        return pages
    }

    /**
     * Split a table into chunks that fit within page boundaries
     */
    private fun splitTable(
        table: TableElement,
        availableWidth: Float,
        firstPageHeight: Float,
        subsequentPageHeight: Float
    ): List<TableElement> {
        val chunks = mutableListOf<TableElement>()
        val colWidths = table.calculateColumnWidths(availableWidth)

        // Find header row (if any)
        val headerRow = table.rows.firstOrNull { it.isHeader }
        val headerHeight = headerRow?.let { measureRowHeight(it, colWidths) } ?: 0f

        var currentChunkRows = mutableListOf<TableRow>()
        var currentChunkHeight = 0f
        var availableHeight = firstPageHeight
        var isFirstChunk = true

        // Add header to first chunk if exists
        if (headerRow != null) {
            currentChunkRows.add(headerRow)
            currentChunkHeight = headerHeight
        }

        // Get data rows only (exclude header)
        val dataRows = table.rows.filter { !it.isHeader }

        for (row in dataRows) {
            val rowHeight = measureRowHeight(row, colWidths)

            // Check if row fits in current chunk
            if (currentChunkHeight + rowHeight <= availableHeight) {
                currentChunkRows.add(row)
                currentChunkHeight += rowHeight
            } else {
                // Current row doesn't fit
                // Save current chunk if it has data rows (not just header)
                val hasDataRows = currentChunkRows.any { !it.isHeader }
                if (hasDataRows) {
                    chunks.add(table.copy(
                        rows = currentChunkRows.toList(),
                        spacingAfter = 0f
                    ))

                    // Start new chunk
                    currentChunkRows = mutableListOf()
                    currentChunkHeight = 0f
                    isFirstChunk = false
                }

                // For new chunks, use subsequent page height
                if (!isFirstChunk || !hasDataRows) {
                    availableHeight = subsequentPageHeight
                }

                // Add header to new chunk if exists
                if (headerRow != null && !currentChunkRows.any { it.isHeader }) {
                    currentChunkRows.add(headerRow)
                    currentChunkHeight = headerHeight
                }

                // Add current row to chunk
                currentChunkRows.add(row)
                currentChunkHeight += rowHeight
                isFirstChunk = false
            }
        }

        // Add final chunk with proper spacing
        if (currentChunkRows.isNotEmpty()) {
            val hasDataRows = currentChunkRows.any { !it.isHeader }
            if (hasDataRows) {
                chunks.add(table.copy(
                    rows = currentChunkRows.toList(),
                    spacingAfter = table.spacingAfter
                ))
            }
        }

        return chunks
    }

    /**
     * Split a list into chunks that fit within page boundaries
     */
    private fun splitList(
        list: ListElement,
        availableWidth: Float,
        firstPageHeight: Float,
        subsequentPageHeight: Float
    ): List<PdfElement> {
        val chunks = mutableListOf<PdfElement>()

        paint.textSize = list.textSize
        paint.typeface = list.typeface

        val effectiveWidth = availableWidth - list.indent

        var currentItems = mutableListOf<String>()
        var currentHeight = 0f
        var isFirstChunk = true
        var availableHeight = firstPageHeight
        var itemOffset = 0

        for ((_, item) in list.items.withIndex()) {
            val lines = TextElement.wrapText(item, effectiveWidth, paint)
            val itemHeight = (lines.size * list.textSize * 1.2f) + list.itemSpacing

            if (currentHeight + itemHeight <= availableHeight) {
                currentItems.add(item)
                currentHeight += itemHeight
            } else {
                // Save current chunk
                if (currentItems.isNotEmpty()) {
                    chunks.add(createListChunk(list, currentItems.toList(), itemOffset, isFirstChunk))
                    itemOffset += currentItems.size
                }

                // Start new chunk
                currentItems = mutableListOf(item)
                currentHeight = itemHeight
                isFirstChunk = false
                availableHeight = subsequentPageHeight
            }
        }

        // Add final chunk
        if (currentItems.isNotEmpty()) {
            chunks.add(createListChunk(list, currentItems.toList(), itemOffset, false))
        }

        return chunks
    }

    private fun createListChunk(
        original: ListElement,
        items: List<String>,
        startIndex: Int,
        removeSpacing: Boolean
    ): PdfElement {
        return if (original.isNumbered) {
            // For numbered lists, we need to create a custom implementation
            // that maintains the correct numbering
            NumberedListChunk(
                items = items,
                startNumber = startIndex + 1,
                textSize = original.textSize,
                textColor = original.textColor,
                typeface = original.typeface,
                indent = original.indent,
                itemSpacing = original.itemSpacing,
                spacingAfter = if (removeSpacing) 0f else original.spacingAfter
            )
        } else {
            original.copy(
                items = items,
                spacingAfter = if (removeSpacing) 0f else original.spacingAfter
            )
        }
    }

    private fun measureRowHeight(row: TableRow, colWidths: List<Float>): Float {
        var maxHeight = row.minHeight.coerceAtLeast(24f)

        row.cells.forEachIndexed { index, cell ->
            if (index < colWidths.size) {
                paint.textSize = cell.textSize
                paint.typeface = cell.typeface

                val cellWidth = colWidths[index] - (cell.padding * 2)
                val lines = TextElement.wrapText(cell.content, cellWidth, paint)
                val cellHeight = (lines.size * cell.textSize * 1.2f) + (cell.padding * 2)
                maxHeight = maxOf(maxHeight, cellHeight)
            }
        }

        return maxHeight
    }

    /**
     * Calculate total number of pages needed for given elements
     */
    fun calculatePageCount(elements: List<PdfElement>): Int {
        return layoutElements(elements).size
    }
}

