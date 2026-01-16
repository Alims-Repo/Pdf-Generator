# 📄 PDF Generator for Android

[![Maven Central](https://img.shields.io/maven-central/v/io.github.alims-repo/pdf-generator.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.alims-repo/pdf-generator)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

A lightweight, powerful Kotlin DSL library for generating multi-page PDF documents on Android. Create professional PDFs with text, tables, images, QR codes, checkboxes, and more — all with automatic pagination support.

---

## ✨ Features

- 📝 **Rich Text Support** - Titles, headings, paragraphs with custom fonts, colors, and alignment
- 📊 **Tables** - Full-featured tables with headers, styling, and automatic page splitting
- 📋 **Lists** - Bullet lists and numbered lists with automatic continuation across pages
- 🖼️ **Images** - Add images with flexible sizing and alignment options
- 📱 **QR Codes** - Generate QR codes for URLs, contacts, WiFi, email, phone, SMS, and more
- ☑️ **Checkboxes** - Perfect for forms, checklists, and surveys
- 📦 **Box Elements** - Info, warning, error, and success boxes for highlighted content
- 🔖 **Headers & Footers** - Customizable with page numbers
- 💧 **Watermarks** - Text watermarks with rotation and transparency
- 📐 **Multiple Page Sizes** - A3, A4, A5, A6, Letter, Legal, or custom sizes
- 🎨 **Full Customization** - Colors, margins, backgrounds, and more
- ⚡ **Automatic Pagination** - Content automatically flows across multiple pages
- 🧩 **Kotlin DSL** - Clean, intuitive syntax for building PDFs

---

## 📦 Installation

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.alims-repo:pdf-generator:1.0.6-beta")
}
```

Or for Groovy `build.gradle`:

```groovy
dependencies {
    implementation 'io.github.alims-repo:pdf-generator:1.0.6-beta'
}
```

---

## 🚀 Quick Start

### Using the DSL (Recommended)

```kotlin
import io.github.alimsrepo.pdf.generator.pdf
import io.github.alimsrepo.pdf.generator.saveToFile

val pdfFile = File(context.cacheDir, "document.pdf")

pdf {
    // Page setup
    pageSize(PageSize.A4)
    orientation(PageOrientation.PORTRAIT)
    margins(PageMargins.NORMAL)
    
    // Content
    title("My Document")
    text("Hello, PDF Generator!")
    
    spacer(16f)
    divider()
    
    heading("Features")
    bulletList(
        "Easy to use",
        "Powerful features",
        "Automatic pagination"
    )
    
    table {
        header("Name", "Age", "City")
        row("John", "28", "New York")
        row("Jane", "32", "London")
    }
}.saveToFile(pdfFile)
```

### Using the Builder API

```kotlin
PdfBuilder()
    .setPageSize(PageSize.A4)
    .setMargins(PageMargins.NORMAL)
    .addTitle("My Document")
    .addText("Hello, PDF Generator!")
    .addSpacer(16f)
    .addDivider()
    .addHeading("Features")
    .addBulletList(listOf("Easy to use", "Powerful features"))
    .build(PdfOutput.ToFile(pdfFile), listener)
```

---

## 📖 Documentation

### Page Configuration

```kotlin
pdf {
    // Standard page sizes
    pageSize(PageSize.A4)        // A3, A4, A5, A6, LETTER, LEGAL, TABLOID, EXECUTIVE
    
    // Custom page size
    pageSizeMm(210f, 297f)       // Width x Height in millimeters
    pageSizeInches(8.5f, 11f)    // Width x Height in inches
    
    // Orientation
    orientation(PageOrientation.PORTRAIT)   // or LANDSCAPE
    
    // Margins
    margins(PageMargins.NORMAL)  // NONE, NARROW, MODERATE, NORMAL, WIDE
    margins(72f, 72f, 72f, 72f)  // Top, Bottom, Left, Right in points
    marginsMm(25f, 25f, 25f, 25f) // In millimeters
    
    // Background color
    backgroundColor(0xFFFFFFF0.toInt())
}
```

### Headers & Footers

```kotlin
pdf {
    header(
        left = "Company Name",
        center = "Document Title",
        right = "{page}/{total}",
        showPageNumber = true
    )
    
    footer(
        center = "© 2026 Company Name",
        showPageNumber = true,
        pageNumberFormat = "Page {page} of {total}"
    )
}
```

### Text Elements

```kotlin
pdf {
    // Pre-styled text
    title("Document Title")           // 24pt bold
    heading("Section Heading")        // 18pt bold
    subheading("Subsection")          // 14pt bold
    text("Regular paragraph text")    // 12pt normal
    
    // Custom styled text
    text(
        content = "Custom text",
        size = 16f,
        color = 0xFF2196F3.toInt(),
        typeface = Typeface.DEFAULT_BOLD,
        align = TextAlign.CENTER
    )
}
```

### Lists

```kotlin
pdf {
    // Bullet list
    bulletList(
        "First item",
        "Second item",
        "Third item"
    )
    
    // Numbered list
    numberedList(
        "Step one",
        "Step two",
        "Step three"
    )
}
```

### Tables

```kotlin
pdf {
    table {
        header("Product", "Price", "Quantity")
        row("Widget", "$9.99", "100")
        row("Gadget", "$19.99", "50")
        row("Gizmo", "$14.99", "75")
    }
}
```

> Tables automatically split across pages when content exceeds the page height, with headers repeated on each page.

### Images

```kotlin
pdf {
    image(
        bitmap = myBitmap,
        width = 200f,           // Optional, uses full width if not specified
        height = 150f,          // Optional, maintains aspect ratio if not specified
        align = TextAlign.CENTER
    )
}
```

### QR Codes

```kotlin
pdf {
    // Simple QR code
    qrCode("https://example.com", size = 150f)
    
    // URL QR code
    qrCodeUrl("https://github.com/user/repo")
    
    // Contact card (vCard)
    qrCodeVCard(
        firstName = "John",
        lastName = "Doe",
        phone = "+1234567890",
        email = "john@example.com",
        organization = "ACME Corp"
    )
    
    // WiFi credentials
    qrCodeWifi(
        ssid = "NetworkName",
        password = "secretpassword"
    )
    
    // Email
    qrCodeEmail(
        email = "contact@example.com",
        subject = "Hello",
        body = "Message body"
    )
    
    // Phone number
    qrCodePhone("+1234567890")
    
    // SMS
    qrCodeSms("+1234567890", message = "Hello!")
    
    // Geographic location
    qrCodeLocation(latitude = 40.7128, longitude = -74.0060)
}
```

### Checkboxes

```kotlin
pdf {
    // Single checkbox
    checkbox("Accept terms and conditions", isChecked = true)
    checkbox("Subscribe to newsletter", isChecked = false)
    
    // Checkbox list with states
    checkboxList(
        listOf(
            CheckboxItem("Task 1", isChecked = true),
            CheckboxItem("Task 2", isChecked = true),
            CheckboxItem("Task 3", isChecked = false)
        )
    )
    
    // Simple checkbox list (all unchecked)
    checkboxList("Option A", "Option B", "Option C")
}
```

### Box Elements

```kotlin
pdf {
    // Info box (blue)
    infoBox(
        TextElement("ℹ️ Information", textSize = 14f, typeface = Typeface.DEFAULT_BOLD),
        TextElement("This is helpful information for the user.")
    )
    
    // Success box (green)
    successBox(TextElement("✓ Operation completed successfully!"))
    
    // Warning box (yellow)
    warningBox(TextElement("⚠️ Please review before proceeding."))
    
    // Error box (red)
    errorBox(TextElement("✗ An error occurred."))
    
    // Custom box
    box(
        elements = listOf(TextElement("Custom content")),
        padding = 16f,
        backgroundColor = 0xFFF5F5F5.toInt()
    )
}
```

### Dividers & Spacers

```kotlin
pdf {
    divider()                           // Default 1pt black line
    divider(thickness = 2f, color = 0xFF4CAF50.toInt())
    dashedDivider()                     // Dashed line
    
    spacer(20f)                         // Vertical space in points
}
```

### Watermarks

```kotlin
pdf {
    // Custom text watermark
    textWatermark(
        text = "CONFIDENTIAL",
        textSize = 60f,
        textColor = 0x33FF0000,  // Semi-transparent red
        rotation = -45f
    )
    
    // Pre-defined watermarks
    draftWatermark()
    confidentialWatermark()
}
```

### Document Metadata

```kotlin
pdf {
    metadata {
        title("Annual Report 2026")
        author("John Doe")
        subject("Financial Summary")
        keywords("finance", "report", "2026")
    }
}
```

### Page Breaks

```kotlin
pdf {
    title("Page 1 Content")
    text("Some content...")
    
    pageBreak()  // Force new page
    
    title("Page 2 Content")
    text("More content...")
}
```

### Output Options

```kotlin
val pdfBuilder = pdf { /* ... */ }

// Save to file
pdfBuilder.saveToFile(File("document.pdf"))

// Save to path
pdfBuilder.saveToPath("/storage/documents", "report")

// Get as byte array
val bytes = pdfBuilder.toByteArray().getOrNull()

// Write to OutputStream
pdfBuilder.toOutputStream(outputStream)

// With listener for progress
pdfBuilder.build(PdfOutput.ToFile(file), object : PdfGenerationListener {
    override fun onStart() { /* Started */ }
    override fun onProgress(currentPage: Int, totalPages: Int) { /* Progress */ }
    override fun onSuccess(result: PdfResult) { /* Success */ }
    override fun onFailure(error: PdfError) { /* Error */ }
})
```

---

## 📱 View-based PDF Generation

Generate PDFs from Android Views:

```kotlin
val pdfBuilder = ViewPdfBuilder.with(context)
    .setPageSize(PageSize.A4)
    .setOrientation(PageOrientation.PORTRAIT)
    .addView(myView)
    .addViewFromLayout(R.layout.my_layout) { view ->
        // Bind data to view
        view.findViewById<TextView>(R.id.title).text = "Hello"
    }

pdfBuilder.build(PdfOutput.ToFile(file))
```

---

## 📐 Page Sizes Reference

| Size | Dimensions | Use Case |
|------|------------|----------|
| A3 | 297mm × 420mm | Posters, Large Documents |
| A4 | 210mm × 297mm | Standard Documents |
| A5 | 148mm × 210mm | Booklets, Flyers |
| A6 | 105mm × 148mm | Postcards |
| Letter | 8.5" × 11" | US Standard |
| Legal | 8.5" × 14" | Legal Documents |
| Tabloid | 11" × 17" | Newspapers |
| Executive | 7.25" × 10.5" | Business |

---

## 🎨 Customization Tips

### Custom Fonts

```kotlin
val customTypeface = Typeface.createFromAsset(assets, "fonts/custom.ttf")
text("Custom font text", typeface = customTypeface)
```

### Colors

All colors use Android's ARGB integer format:

```kotlin
val red = 0xFFFF0000.toInt()
val semiTransparent = 0x80000000.toInt()  // 50% transparent black
```

### Element Spacing

Most elements have a `spacingAfter` parameter:

```kotlin
text("No extra space after", spacingAfter = 0f)
```

---

## 📋 Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Language**: Kotlin
- **Dependencies**: AndroidX Core, ZXing (for QR codes)

---

## 📄 License

```
Copyright 2026 Alim Sourav

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📬 Contact

- **Author**: Alim Sourav
- **Email**: sourav.0.alim@gmail.com
- **GitHub**: [@Alims-Repo](https://github.com/Alims-Repo)

---

## ⭐ Show Your Support

If you find this library helpful, please give it a ⭐ on GitHub!

---

<p align="center">Made with ❤️ for the Android community</p>

