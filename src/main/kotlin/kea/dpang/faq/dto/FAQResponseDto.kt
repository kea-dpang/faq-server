package kea.dpang.faq.dto

import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import java.time.LocalDateTime
import java.util.*

data class FAQResponseDto(
    val postId: Long,
    val question: String,
    val answer: String,
    val category: Category,
    val authorId: UUID,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(faq: FAQ): FAQResponseDto {
            return FAQResponseDto(
                postId = faq.id!!,
                question = faq.question,
                answer = faq.answer,
                category = faq.category,
                authorId = faq.authorId,
                createdAt = faq.createdAt!!
            )
        }
    }
}