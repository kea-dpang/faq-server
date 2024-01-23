package kea.dpang.faq.dto

import java.time.LocalDateTime
import java.util.*

data class PostResponseDto(
    val postId: Long,
    val question: String,
    val answer: String,
    val categoryName: String,
    val authorId: UUID,
    val createdAt: LocalDateTime
)