package io.github.alimsrepo.pdf.generator.content

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * QR Code element for generating QR codes in PDFs
 *
 * Usage:
 * ```kotlin
 * pdf {
 *     qrCode("https://example.com")
 *     qrCode("Contact: John Doe", size = 200f, align = TextAlign.CENTER)
 * }
 * ```
 */
data class QRCodeElement(
    val data: String,
    val size: Float = 150f,
    val alignment: TextAlign = TextAlign.CENTER,
    val foregroundColor: Int = 0xFF000000.toInt(),
    val backgroundColor: Int = 0x00000000.toInt(),//0xFFFFFFFF.toInt(),
    val errorCorrectionLevel: QRErrorCorrectionLevel = QRErrorCorrectionLevel.MEDIUM,
    val margin: Int = 1,
    val spacingAfter: Float = 8f
) : PdfElement {

    override fun measureHeight(availableWidth: Float): Float {
        return size + spacingAfter
    }

    /**
     * Generate the QR code as a Bitmap
     */
    fun generateBitmap(): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>(
            EncodeHintType.ERROR_CORRECTION to when (errorCorrectionLevel) {
                QRErrorCorrectionLevel.LOW -> ErrorCorrectionLevel.L
                QRErrorCorrectionLevel.MEDIUM -> ErrorCorrectionLevel.M
                QRErrorCorrectionLevel.QUARTILE -> ErrorCorrectionLevel.Q
                QRErrorCorrectionLevel.HIGH -> ErrorCorrectionLevel.H
            },
            EncodeHintType.MARGIN to margin,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size.toInt(), size.toInt(), hints)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    companion object {
        /**
         * Create a QR code for a URL
         */
        fun url(url: String, size: Float = 150f) = QRCodeElement(
            data = url,
            size = size,
            errorCorrectionLevel = QRErrorCorrectionLevel.MEDIUM
        )

        /**
         * Create a QR code for plain text
         */
        fun text(text: String, size: Float = 150f) = QRCodeElement(
            data = text,
            size = size,
            errorCorrectionLevel = QRErrorCorrectionLevel.MEDIUM
        )

        /**
         * Create a QR code for email
         */
        fun email(email: String, subject: String? = null, body: String? = null, size: Float = 150f): QRCodeElement {
            val data = buildString {
                append("mailto:$email")
                val params = mutableListOf<String>()
                subject?.let { params.add("subject=${it}") }
                body?.let { params.add("body=${it}") }
                if (params.isNotEmpty()) {
                    append("?${params.joinToString("&")}")
                }
            }
            return QRCodeElement(data = data, size = size)
        }

        /**
         * Create a QR code for phone number
         */
        fun phone(phoneNumber: String, size: Float = 150f) = QRCodeElement(
            data = "tel:$phoneNumber",
            size = size
        )

        /**
         * Create a QR code for SMS
         */
        fun sms(phoneNumber: String, message: String? = null, size: Float = 150f): QRCodeElement {
            val data = if (message != null) "sms:$phoneNumber?body=$message" else "sms:$phoneNumber"
            return QRCodeElement(data = data, size = size)
        }

        /**
         * Create a QR code for WiFi network
         */
        fun wifi(
            ssid: String,
            password: String? = null,
            securityType: WifiSecurityType = WifiSecurityType.WPA,
            hidden: Boolean = false,
            size: Float = 150f
        ): QRCodeElement {
            val data = buildString {
                append("WIFI:")
                append("T:${securityType.name};")
                append("S:$ssid;")
                password?.let { append("P:$it;") }
                if (hidden) append("H:true;")
                append(";")
            }
            return QRCodeElement(data = data, size = size)
        }

        /**
         * Create a QR code for vCard contact
         */
        fun vCard(
            firstName: String,
            lastName: String? = null,
            phone: String? = null,
            email: String? = null,
            organization: String? = null,
            title: String? = null,
            address: String? = null,
            website: String? = null,
            size: Float = 150f
        ): QRCodeElement {
            val data = buildString {
                append("BEGIN:VCARD\n")
                append("VERSION:3.0\n")
                append("N:${lastName ?: ""};$firstName;;;\n")
                append("FN:$firstName${lastName?.let { " $it" } ?: ""}\n")
                organization?.let { append("ORG:$it\n") }
                title?.let { append("TITLE:$it\n") }
                phone?.let { append("TEL:$it\n") }
                email?.let { append("EMAIL:$it\n") }
                address?.let { append("ADR:;;$it;;;;\n") }
                website?.let { append("URL:$it\n") }
                append("END:VCARD")
            }
            return QRCodeElement(data = data, size = size, errorCorrectionLevel = QRErrorCorrectionLevel.MEDIUM)
        }

        /**
         * Create a QR code for geographic location
         */
        fun location(latitude: Double, longitude: Double, size: Float = 150f) = QRCodeElement(
            data = "geo:$latitude,$longitude",
            size = size
        )
    }
}