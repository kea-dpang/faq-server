package kea.dpang.faq.dto

data class PostUpdateRequestDto(
    val categoryId: Int,
    val question: String,
    val answer: String
)