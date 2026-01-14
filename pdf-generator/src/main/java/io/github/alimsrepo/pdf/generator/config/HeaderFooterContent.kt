package io.github.alimsrepo.pdf.generator.config

import android.graphics.Typeface

/**
 * Content configuration for headers/footers
 */
data class HeaderFooterContent(
    val leftText: String? = null,
    val centerText: String? = null,
    val rightText: String? = null,
    val showPageNumber: Boolean = false,
    val pageNumberFormat: String = "Page {page} of {total}",
    val textSize: Float = 10f,
    val textColor: Int = 0xFF000000.toInt(),
    val typeface: Typeface = Typeface.DEFAULT
)