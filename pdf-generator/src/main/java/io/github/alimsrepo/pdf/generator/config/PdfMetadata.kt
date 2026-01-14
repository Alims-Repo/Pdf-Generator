package io.github.alimsrepo.pdf.generator.config

/**
 * PDF Document metadata
 * Note: Android's PdfDocument has limited metadata support,
 * but this class prepares for future enhancements or alternative PDF libraries.
 */
data class PdfMetadata(
    val title: String? = null,
    val author: String? = null,
    val subject: String? = null,
    val keywords: List<String>? = null,
    val creator: String? = "PDF Generator Library",
    val producer: String? = null,
    val creationDate: Long? = System.currentTimeMillis(),
    val modificationDate: Long? = null
) {
    companion object {
        /**
         * Create empty metadata
         */
        val EMPTY = PdfMetadata()

        /**
         * Create metadata with just a title
         */
        fun withTitle(title: String) = PdfMetadata(title = title)

        /**
         * Create metadata with title and author
         */
        fun withTitleAndAuthor(title: String, author: String) = PdfMetadata(
            title = title,
            author = author
        )
    }

    /**
     * Builder for creating metadata
     */
    class Builder {
        private var title: String? = null
        private var author: String? = null
        private var subject: String? = null
        private var keywords: MutableList<String>? = null
        private var creator: String? = "PDF Generator Library"
        private var producer: String? = null
        private var creationDate: Long? = System.currentTimeMillis()
        private var modificationDate: Long? = null

        fun title(title: String) = apply { this.title = title }
        fun author(author: String) = apply { this.author = author }
        fun subject(subject: String) = apply { this.subject = subject }
        fun keywords(vararg keywords: String) = apply {
            this.keywords = keywords.toMutableList()
        }
        fun addKeyword(keyword: String) = apply {
            if (this.keywords == null) this.keywords = mutableListOf()
            this.keywords!!.add(keyword)
        }
        fun creator(creator: String) = apply { this.creator = creator }
        fun producer(producer: String) = apply { this.producer = producer }
        fun creationDate(timestamp: Long) = apply { this.creationDate = timestamp }
        fun modificationDate(timestamp: Long) = apply { this.modificationDate = timestamp }

        fun build() = PdfMetadata(
            title = title,
            author = author,
            subject = subject,
            keywords = keywords?.toList(),
            creator = creator,
            producer = producer,
            creationDate = creationDate,
            modificationDate = modificationDate
        )
    }
}

