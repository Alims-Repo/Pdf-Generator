package com.alim.pdfgenerator

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.alim.pdfgenerator.ui.theme.PdfGeneratorTheme
import io.github.alimsrepo.pdf.generator.config.PageMargins
import io.github.alimsrepo.pdf.generator.config.PageOrientation
import io.github.alimsrepo.pdf.generator.config.PageSize
import io.github.alimsrepo.pdf.generator.content.TextAlign
import io.github.alimsrepo.pdf.generator.content.TextElement
import io.github.alimsrepo.pdf.generator.pdf
import io.github.alimsrepo.pdf.generator.saveToFile
import java.io.File
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

                    val pdfFile = File(cacheDir, "nelu_code_invoice.pdf")

                    // Brand Colors
                    val primaryColor = 0xFF1E3A5F.toInt()      // Deep Navy
                    val accentColor = 0xFF4CAF50.toInt()       // Green
                    val textDark = 0xFF2C3E50.toInt()          // Dark text
                    val textMuted = 0xFF7F8C8D.toInt()         // Muted gray
                    val bgLight = 0xFFF8FAFC.toInt()           // Light background
                    val white = 0xFFFFFFFF.toInt()

                    LaunchedEffect(Unit) {
                        processTime = measureTimeMillis {
                            pdf {
                                pageSize(PageSize.A4)
                                orientation(PageOrientation.PORTRAIT)
                                margins(PageMargins.NORMAL)
                                backgroundColor(white)

                                metadata {
                                    title("Nelu Code - Invoice #NC-2026-0116")
                                    author("Nelu Code")
                                    subject("Professional Services Invoice")
                                }

                                // ═══════════════════════════════════════════════
                                // HEADER SECTION
                                // ═══════════════════════════════════════════════

                                // Company Logo/Name
                                text("NELU CODE", size = 32f, color = primaryColor, typeface = Typeface.DEFAULT_BOLD)
                                text("Crafting Digital Excellence", size = 11f, color = textMuted, typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC))
                                spacer(20f)

                                // Brand Accent Line
                                divider(thickness = 4f, color = accentColor)
                                spacer(24f)

                                // ═══════════════════════════════════════════════
                                // INVOICE INFO ROW
                                // ═══════════════════════════════════════════════

                                text("INVOICE", size = 28f, align = TextAlign.RIGHT, color = primaryColor, typeface = Typeface.DEFAULT_BOLD)
                                spacer(6f)
                                text("#NC-2026-0116", size = 14f, align = TextAlign.RIGHT, color = accentColor, typeface = Typeface.DEFAULT_BOLD)
                                spacer(20f)

                                // Date Box
                                box(
                                    listOf(
                                        TextElement("Issue Date:    January 16, 2026", textSize = 11f, textColor = textDark),
                                        TextElement("Due Date:      February 16, 2026", textSize = 11f, textColor = textDark),
                                        TextElement("Status:           PENDING", textSize = 11f, textColor = 0xFFE67E22.toInt(), typeface = Typeface.DEFAULT_BOLD)
                                    ),
                                    padding = 14f,
                                    backgroundColor = bgLight
                                )
                                spacer(24f)

                                // ═══════════════════════════════════════════════
                                // FROM / TO SECTION
                                // ═══════════════════════════════════════════════

                                // From Section
                                text("FROM", size = 9f, color = textMuted, typeface = Typeface.DEFAULT_BOLD)
                                spacer(4f)
                                text("Nelu Code", size = 13f, color = textDark, typeface = Typeface.DEFAULT_BOLD)
                                text("nelucode@gmail.com", size = 11f, color = accentColor)
                                text("Software Development Services", size = 10f, color = textMuted)
                                spacer(16f)

                                // To Section
                                box(
                                    listOf(
                                        TextElement("BILL TO", textSize = 9f, textColor = textMuted, typeface = Typeface.DEFAULT_BOLD),
                                        TextElement("", textSize = 4f),
                                        TextElement("TechVentures Inc.", textSize = 14f, textColor = textDark, typeface = Typeface.DEFAULT_BOLD),
                                        TextElement("456 Innovation Drive, Suite 200", textSize = 11f, textColor = textDark),
                                        TextElement("San Francisco, CA 94102", textSize = 11f, textColor = textDark),
                                        TextElement("billing@techventures.io", textSize = 11f, textColor = accentColor)
                                    ),
                                    padding = 16f,
                                    backgroundColor = bgLight
                                )
                                spacer(28f)

                                // ═══════════════════════════════════════════════
                                // SERVICES TABLE
                                // ═══════════════════════════════════════════════

                                text("SERVICES", size = 10f, color = textMuted, typeface = Typeface.DEFAULT_BOLD)
                                spacer(10f)

                                table {
                                    header("Service Description", "Hours", "Rate", "Total")
                                    row("Mobile App Development (Android/iOS)", "48", "\$85", "\$4,080")
                                    row("Backend API Development", "24", "\$90", "\$2,160")
                                    row("UI/UX Design & Prototyping", "16", "\$75", "\$1,200")
                                    row("Code Review & Optimization", "8", "\$80", "\$640")
                                    row("Documentation & Training", "4", "\$60", "\$240")
                                }
                                spacer(20f)

                                // ═══════════════════════════════════════════════
                                // TOTALS SECTION
                                // ═══════════════════════════════════════════════

                                divider(thickness = 1f, color = 0xFFEEEEEE.toInt())
                                spacer(12f)

                                text("Subtotal:                                    \$8,320.00", size = 12f, align = TextAlign.RIGHT, color = textDark)
                                spacer(4f)
                                text("Tax (8%):                                       \$665.60", size = 12f, align = TextAlign.RIGHT, color = textMuted)
                                spacer(4f)
                                text("Discount:                                        -\$0.00", size = 12f, align = TextAlign.RIGHT, color = textMuted)
                                spacer(12f)

                                divider(thickness = 2f, color = primaryColor)
                                spacer(12f)

                                text("AMOUNT DUE:              \$8,985.60", size = 18f, align = TextAlign.RIGHT, color = primaryColor, typeface = Typeface.DEFAULT_BOLD)
                                spacer(28f)

                                // ═══════════════════════════════════════════════
                                // PAYMENT SECTION
                                // ═══════════════════════════════════════════════

                                infoBox(
                                    TextElement("PAYMENT DETAILS", textSize = 10f, textColor = textMuted, typeface = Typeface.DEFAULT_BOLD),
                                    TextElement("", textSize = 6f),
                                    TextElement("Bank Transfer", textSize = 12f, textColor = textDark, typeface = Typeface.DEFAULT_BOLD),
                                    TextElement("Bank:  First National Bank", textSize = 11f, textColor = textDark),
                                    TextElement("Name:  Nelu Code LLC", textSize = 11f, textColor = textDark),
                                    TextElement("A/C:    ●●●● ●●●● 4521", textSize = 11f, textColor = textDark),
                                    TextElement("", textSize = 8f),
                                    TextElement("Or scan QR code to pay via email →", textSize = 10f, textColor = accentColor)
                                )
                                spacer(16f)

                                // QR Code
                                qrCodeEmail(
                                    email = "nelucode@gmail.com",
                                    subject = "Payment: Invoice #NC-2026-0116",
                                    body = "Payment for Invoice #NC-2026-0116\nAmount: \$8,985.60\n\nThank you for your business!",
                                    size = 90f,
                                    align = TextAlign.CENTER
                                )
                                spacer(28f)

                                // ═══════════════════════════════════════════════
                                // FOOTER
                                // ═══════════════════════════════════════════════

                                divider(thickness = 1f, color = 0xFFEEEEEE.toInt())
                                spacer(16f)

                                text("Thank you for choosing Nelu Code!", size = 13f, align = TextAlign.CENTER, color = primaryColor, typeface = Typeface.DEFAULT_BOLD)
                                spacer(4f)
                                text("Questions? Reach us at nelucode@gmail.com", size = 10f, align = TextAlign.CENTER, color = textMuted)
                                spacer(8f)
                                text("Payment is due within 30 days. Late payments may incur a 2% monthly fee.", size = 9f, align = TextAlign.CENTER, color = textMuted, typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC))

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
                                    Text(text = "View Invoice")
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