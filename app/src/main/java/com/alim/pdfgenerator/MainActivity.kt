package com.alim.pdfgenerator

import android.content.Intent
import android.graphics.Bitmap
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
import io.github.alimsrepo.pdf.generator.config.PageSize
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

                    val pdfFile = File(cacheDir, "preview.pdf")

                    LaunchedEffect(Unit) {
                        processTime = measureTimeMillis {
                            pdf {
                                pageSize(PageSize.A4)
                                margins(PageMargins.NORMAL)

                                // Page 1 - Title and Introduction
                                title("Lawyers Diary - Case Report", align = TextAlign.CENTER)
                                text("Generated on: January 14, 2026")
                                spacer(20f)

                                heading("Executive Summary")
                                text("This comprehensive report contains all case details, client information, and hearing schedules for the current month. The document demonstrates the multi-page PDF generation capabilities with automatic pagination.")
                                spacer(16f)

                                divider()
                                spacer(16f)

                                // Section 2 - Client List
                                heading("Client Directory")
                                text("Below is the complete list of active clients registered in the system:")
                                spacer(8f)

                                table {
                                    header("ID", "Client Name", "Case Type", "Status")
                                    row("001", "Mohammad Rahman", "Civil", "Active")
                                    row("002", "Fatima Begum", "Criminal", "Pending")
                                    row("003", "Abdul Karim", "Family", "Active")
                                    row("004", "Nasreen Akter", "Property", "Closed")
                                    row("005", "Jamal Uddin", "Corporate", "Active")
                                }
                                spacer(20f)

                                // Section 3 - Large Table (will span multiple pages)
                                heading("Complete Case Register")
                                text("Detailed listing of all 100 registered cases:")
                                spacer(8f)

                                table {
                                    header("Case #", "Title", "Court", "Next Hearing")
                                    for (x in 1..100) {
                                        row(
                                            "CAS-${String.format("%03d", x)}",
                                            "Case Title $x",
                                            if (x % 3 == 0) "High Court" else if (x % 2 == 0) "District Court" else "Supreme Court",
                                            "2026-${
                                                String.format(
                                                    "%02d",
                                                    (x % 12) + 1
                                                )
                                            }-${String.format("%02d", (x % 28) + 1)}"
                                        )
                                    }
                                }
                                spacer(20f)

                                // Final Section - Notes
                                heading("Important Notes")
                                bulletList(
                                    "All hearing dates are subject to change",
                                    "Contact the court clerk for confirmation",
                                    "Keep original documents ready",
                                    "Arrive 30 minutes before scheduled time"
                                )
                                spacer(16f)

                                divider()
                                text(
                                    "© 2026 Lawyers Diary - Confidential Document",
                                    align = TextAlign.CENTER
                                )

                                textWatermark("DRAFT")
                                draftWatermark()
                                confidentialWatermark()

                                // New metadata
                                metadata {
                                    title("Document")
                                    author("Author")
                                }

                                box(
                                    listOf(
                                        TextElement("This is text 0"),
                                        TextElement("This is text 1"),
                                        TextElement("This is text 2"),
                                        TextElement("This is text 3")
                                    )
                                )

                                // New box elements
                                infoBox(TextElement("This is info Box"))
                                warningBox(TextElement("This is warning Box"))

                                // New checkbox elements
                                checkbox("Accept terms", isChecked = true)
                                checkboxList("Option 1", "Option 2")

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

                        Log.e("Intent", intent.toString())
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            intent?.let {
                                Button(onClick = { startActivity(intent) }) {
                                    Text(text = "Preview")
                                }
                            } ?: CircularProgressIndicator()

                            processTime?.let { Text(it.toString()) }
                        }
                    }
                }
            }
        }
    }
}