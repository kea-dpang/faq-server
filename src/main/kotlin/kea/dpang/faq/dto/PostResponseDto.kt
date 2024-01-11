package kea.dpang.faq.dto

import java.time.LocalDateTime
import java.util.UUID

data class PostResponse(
    val postId: Int,
    val question: String,
    val answer: String,
    val categoryId: Int,
    val authorId: UUID,
    val createdAt: LocalDateTime
)
