package io.github.alimsrepo.pdf.generator.content

/**
 * QR Code error correction levels
 */
enum class QRErrorCorrectionLevel {
    /** ~7% error correction */
    LOW,
    /** ~15% error correction */
    MEDIUM,
    /** ~25% error correction */
    QUARTILE,
    /** ~30% error correction */
    HIGH
}