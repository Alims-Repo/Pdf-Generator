package com.alim.pdfgenerator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.alim.pdfgenerator.ui.theme.PdfGeneratorTheme
import io.github.alimsrepo.pdf.generator.config.PageMargins
import io.github.alimsrepo.pdf.generator.config.PageOrientation
import io.github.alimsrepo.pdf.generator.config.PageSize
import io.github.alimsrepo.pdf.generator.config.Watermark
import io.github.alimsrepo.pdf.generator.content.CheckboxItem
import io.github.alimsrepo.pdf.generator.content.TextAlign
import io.github.alimsrepo.pdf.generator.content.TextElement
import io.github.alimsrepo.pdf.generator.pdf
import io.github.alimsrepo.pdf.generator.saveToFile
import io.github.alimsrepo.pdf.generator.viewPdf
import java.io.File
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfGeneratorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    var intent by remember {
                        mutableStateOf<Intent?>(null)
                    }

                    var processTime by remember {
                        mutableStateOf<Long?>(null)
                    }

                    val context = LocalContext.current

                    val pdfFile = File(cacheDir, "feature_showcase.pdf")

                    LaunchedEffect(Unit) {
                        processTime = measureTimeMillis {
                            pdf {
                                // ========================================
                                // PAGE CONFIGURATION
                                // ========================================
                                pageSize(PageSize.A4)
                                orientation(PageOrientation.PORTRAIT)
                                margins(PageMargins.NORMAL)
                                backgroundColor(0xFFFFFFF0.toInt()) // Light cream background

                                // Header & Footer
                                header(
                                    left = "PDF Generator Library",
                                    right = "{page}/{total}",
                                    showPageNumber = true
                                )
                                footer(
                                    center = "© 2026 - Feature Showcase Document",
                                    showPageNumber = false
                                )

                                // Document Metadata
                                metadata {
                                    title("PDF Generator Feature Showcase")
                                    author("PDF Generator Library")
                                    subject("Complete Feature Demonstration")
                                    keywords("pdf", "android", "kotlin", "showcase")
                                }

                                // Watermark (will appear on all pages)
                                textWatermark("SAMPLE", textSize = 60f, textColor = 0x15000000, rotation = -30f)

                                // ========================================
                                // PAGE 1: TITLE & INTRODUCTION
                                // ========================================
                                title("PDF Generator Library", size = 28f, align = TextAlign.CENTER)
                                text("Complete Feature Showcase", size = 16f, align = TextAlign.CENTER)
                                spacer(8f)
                                text("Generated on: January 15, 2026", size = 10f, align = TextAlign.CENTER, color = 0xFF666666.toInt())
                                spacer(24f)

                                divider(thickness = 2f)
                                spacer(16f)

                                // Introduction Box
                                infoBox(
                                    TextElement(
                                        text = "Welcome to PDF Generator!",
                                        textSize = 14f,
                                        typeface = Typeface.DEFAULT_BOLD
                                    ),
                                    TextElement(
                                        text = "This document demonstrates all the features available in the PDF Generator library for Android. Each section showcases different capabilities.",
                                        textSize = 12f
                                    )
                                )
                                spacer(20f)

                                // ========================================
                                // TEXT STYLES SECTION
                                // ========================================
                                heading("1. Text Styles", size = 20f)
                                spacer(8f)

                                title("Title Text (24pt default)")
                                heading("Heading Text (18pt default)")
                                subheading("Subheading Text (14pt default)")
                                text("Regular paragraph text (12pt default). This demonstrates the standard body text that wraps automatically when it exceeds the available width of the content area.")
                                spacer(8f)

                                // Text Alignment
                                text("Left aligned text (default)", align = TextAlign.LEFT)
                                text("Center aligned text", align = TextAlign.CENTER)
                                text("Right aligned text", align = TextAlign.RIGHT)
                                spacer(8f)

                                // Custom styled text
                                text("Custom colored text", size = 14f, color = 0xFF2196F3.toInt())
                                text("Bold text example", size = 14f, typeface = Typeface.DEFAULT_BOLD)
                                spacer(16f)

                                // ========================================
                                // DIVIDERS SECTION
                                // ========================================
                                heading("2. Dividers & Spacers")
                                spacer(8f)

                                text("Solid divider (1pt):")
                                divider(thickness = 1f)
                                spacer(8f)

                                text("Thick divider (3pt):")
                                divider(thickness = 3f)
                                spacer(8f)

                                text("Colored divider:")
                                divider(thickness = 2f, color = 0xFF4CAF50.toInt())
                                spacer(8f)

                                text("Dashed divider:")
                                dashedDivider(thickness = 1f)
                                spacer(16f)

                                // ========================================
                                // LISTS SECTION
                                // ========================================
                                heading("3. Lists")
                                spacer(8f)

                                subheading("Bullet List:")
                                bulletList(
                                    "First bullet point item",
                                    "Second bullet point with longer text that may wrap to multiple lines",
                                    "Third item",
                                    "Fourth item",
                                    "Fifth item"
                                )
                                spacer(12f)

                                subheading("Numbered List:")
                                numberedList(
                                    "First numbered item",
                                    "Second numbered item",
                                    "Third numbered item",
                                    "Fourth numbered item",
                                    "Fifth numbered item"
                                )
                                spacer(16f)

                                // ========================================
                                // PAGE BREAK - NEW PAGE
                                // ========================================
                                pageBreak()

                                // ========================================
                                // PAGE 2: TABLES
                                // ========================================
                                heading("4. Tables", size = 20f)
                                spacer(8f)

                                subheading("Simple Table:")
                                table {
                                    header("Name", "Role", "Department")
                                    row("John Smith", "Developer", "Engineering")
                                    row("Jane Doe", "Designer", "UX Team")
                                    row("Bob Wilson", "Manager", "Operations")
                                }
                                spacer(16f)

                                subheading("Data Table with Many Rows:")
                                text("This table demonstrates automatic page splitting when content exceeds page height.")
                                spacer(8f)

                                table {
                                    header("ID", "Product", "Category", "Price")
                                    for (i in 1..25) {
                                        row(
                                            String.format("%03d", i),
                                            "Product Item $i",
                                            when (i % 4) {
                                                0 -> "Electronics"
                                                1 -> "Clothing"
                                                2 -> "Books"
                                                else -> "Home & Garden"
                                            },
                                            "$${(i * 10) + 99}.99"
                                        )
                                    }
                                }
                                spacer(20f)

                                // ========================================
                                // BOXES SECTION
                                // ========================================
                                heading("5. Box Elements", size = 20f)
                                spacer(8f)

                                text("Boxes are container elements with borders, padding, and backgrounds:")
                                spacer(8f)

                                // Custom Box
                                box(
                                    listOf(
                                        TextElement("Custom Box", textSize = 14f, typeface = Typeface.DEFAULT_BOLD),
                                        TextElement("This is a custom box with specified padding and background color.")
                                    ),
                                    padding = 16f,
                                    backgroundColor = 0xFFF5F5F5.toInt()
                                )
                                spacer(12f)

                                // Info Box
                                infoBox(
                                    TextElement("ℹ️ Info Box", textSize = 14f, typeface = Typeface.DEFAULT_BOLD),
                                    TextElement("Use info boxes to highlight important information or tips for the reader.")
                                )
                                spacer(12f)

                                // Success Box
                                successBox(
                                    TextElement("✓ Success Box", textSize = 14f, typeface = Typeface.DEFAULT_BOLD),
                                    TextElement("Success boxes are great for confirmation messages or positive outcomes.")
                                )
                                spacer(12f)

                                // Warning Box
                                warningBox(
                                    TextElement("⚠️ Warning Box", textSize = 14f, typeface = Typeface.DEFAULT_BOLD),
                                    TextElement("Use warning boxes to alert users about potential issues or important notices.")
                                )
                                spacer(12f)

                                // Error Box
                                errorBox(
                                    TextElement("✗ Error Box", textSize = 14f, typeface = Typeface.DEFAULT_BOLD),
                                    TextElement("Error boxes highlight critical errors or things that need immediate attention.")
                                )
                                spacer(20f)

                                // ========================================
                                // PAGE BREAK - NEW PAGE
                                // ========================================
                                pageBreak()

                                // ========================================
                                // PAGE 3: CHECKBOXES & FORMS
                                // ========================================
                                heading("6. Checkbox Elements", size = 20f)
                                spacer(8f)

                                text("Checkboxes are useful for forms, checklists, and task lists:")
                                spacer(12f)

                                subheading("Individual Checkboxes:")
                                checkbox("Unchecked item", isChecked = false)
                                checkbox("Checked item", isChecked = true)
                                checkbox("Another unchecked item", isChecked = false)
                                spacer(16f)

                                subheading("Task List (CheckboxList):")
                                checkboxList(
                                    listOf(
                                        CheckboxItem("Complete project documentation", isChecked = true),
                                        CheckboxItem("Review code changes", isChecked = true),
                                        CheckboxItem("Run unit tests", isChecked = true),
                                        CheckboxItem("Deploy to staging", isChecked = false),
                                        CheckboxItem("Get QA approval", isChecked = false),
                                        CheckboxItem("Deploy to production", isChecked = false)
                                    )
                                )
                                spacer(16f)

                                subheading("Survey Options:")
                                checkboxList(
                                    "Very Satisfied",
                                    "Satisfied",
                                    "Neutral",
                                    "Dissatisfied",
                                    "Very Dissatisfied"
                                )
                                spacer(20f)

                                // ========================================
                                // WATERMARK EXAMPLES
                                // ========================================
                                heading("7. Watermark Support", size = 20f)
                                spacer(8f)

                                text("The library supports text watermarks that appear on all pages. You can customize:")
                                spacer(8f)

                                bulletList(
                                    "Watermark text content",
                                    "Font size (default: 48pt)",
                                    "Color with transparency (default: semi-transparent)",
                                    "Rotation angle (default: -45°)",
                                    "Position on page"
                                )
                                spacer(8f)

                                infoBox(
                                    TextElement(
                                        text = "This document uses a 'SAMPLE' watermark with custom settings. Notice it appears consistently across all pages.",
                                        textSize = 11f
                                    )
                                )
                                spacer(8f)

                                text("Built-in watermark presets:")
                                bulletList(
                                    "draftWatermark() - Adds 'DRAFT' watermark",
                                    "confidentialWatermark() - Adds 'CONFIDENTIAL' watermark",
                                    "textWatermark() - Custom text with full control"
                                )
                                spacer(20f)

                                // ========================================
                                // ADDITIONAL FEATURES
                                // ========================================
                                heading("8. Additional Features", size = 20f)
                                spacer(8f)

                                subheading("Page Sizes Supported:")
                                table {
                                    header("Size", "Dimensions", "Use Case")
                                    row("A3", "297mm × 420mm", "Posters, Large Documents")
                                    row("A4", "210mm × 297mm", "Standard Documents")
                                    row("A5", "148mm × 210mm", "Booklets, Flyers")
                                    row("A6", "105mm × 148mm", "Postcards")
                                    row("Letter", "8.5\" × 11\"", "US Standard")
                                    row("Legal", "8.5\" × 14\"", "Legal Documents")
                                    row("Custom", "User Defined", "Any Size")
                                }
                                spacer(16f)

                                subheading("Margin Presets:")
                                bulletList(
                                    "NONE - No margins",
                                    "NARROW - 0.5 inch all around",
                                    "MODERATE - 0.75 inch all around",
                                    "NORMAL - 1 inch all around (default)",
                                    "WIDE - 1.5 inch all around",
                                    "Custom margins in points, mm, or inches"
                                )
                                spacer(16f)

                                subheading("Output Options:")
                                bulletList(
                                    "Save to File - Direct file output",
                                    "Save to Path - Specify directory and filename",
                                    "ByteArray - In-memory PDF data",
                                    "OutputStream - Write to any output stream"
                                )
                                spacer(20f)

                                // ========================================
                                // PAGE BREAK - NEW PAGE
                                // ========================================
                                pageBreak()

                                // ========================================
                                // PAGE: QR CODES
                                // ========================================
                                heading("9. QR Code Generation", size = 20f)
                                spacer(8f)

                                text("The library supports generating various types of QR codes:")
                                spacer(12f)

                                subheading("Simple QR Code (URL):")
                                text("Scan to visit the project repository:", size = 11f)
                                spacer(4f)
                                qrCodeUrl("https://github.com/user/pdf-generator", size = 120f)
                                spacer(16f)

                                subheading("Contact QR Code (vCard):")
                                text("Scan to add contact:", size = 11f)
                                spacer(4f)
                                qrCodeVCard(
                                    firstName = "John",
                                    lastName = "Doe",
                                    phone = "+1234567890",
                                    email = "john.doe@example.com",
                                    organization = "ACME Corp",
                                    size = 120f
                                )
                                spacer(16f)

                                subheading("WiFi QR Code:")
                                text("Scan to connect to WiFi:", size = 11f)
                                spacer(4f)
                                qrCodeWifi(
                                    ssid = "MyNetwork",
                                    password = "secretpassword123",
                                    size = 120f
                                )
                                spacer(16f)

                                subheading("Other QR Code Types:")
                                bulletList(
                                    "qrCode() - Plain text or custom data",
                                    "qrCodeUrl() - Website URLs",
                                    "qrCodeEmail() - Email with subject/body",
                                    "qrCodePhone() - Phone numbers",
                                    "qrCodeSms() - SMS with message",
                                    "qrCodeWifi() - WiFi network credentials",
                                    "qrCodeVCard() - Contact cards",
                                    "qrCodeLocation() - Geographic coordinates"
                                )
                                spacer(8f)

                                infoBox(
                                    TextElement(
                                        text = "QR codes support customizable size, alignment, colors, and error correction levels (L, M, Q, H).",
                                        textSize = 11f
                                    )
                                )
                                spacer(20f)

                                // ========================================
                                // FOOTER / CLOSING
                                // ========================================
                                divider(thickness = 2f)
                                spacer(16f)

                                text(
                                    "End of Feature Showcase",
                                    size = 16f,
                                    align = TextAlign.CENTER,
                                    typeface = Typeface.DEFAULT_BOLD
                                )
                                spacer(8f)
                                text(
                                    "PDF Generator Library for Android",
                                    size = 12f,
                                    align = TextAlign.CENTER,
                                    color = 0xFF666666.toInt()
                                )
                                text(
                                    "github.com/alimsrepo/pdf-generator",
                                    size = 10f,
                                    align = TextAlign.CENTER,
                                    color = 0xFF2196F3.toInt()
                                )

                            }.saveToFile(pdfFile)
                        }

                        val uri = FileProvider.getUriForFile(
                            this@MainActivity,
                            "${packageName}.provider",
                            pdfFile
                        )

                        intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }

                        Log.d("PdfGenerator", "PDF generated in ${processTime}ms")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "PDF Generator",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            intent?.let {
                                Button(onClick = { startActivity(intent) }) {
                                    Text(text = "View PDF Showcase")
                                }

                                processTime?.let {
                                    Text(
                                        text = "Generated in ${it}ms",
                                        color = androidx.compose.ui.graphics.Color.Gray
                                    )
                                }
                            } ?: Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(text = "Generating PDF...")
                            }
                        }
                    }
                }
            }
        }
    }
}