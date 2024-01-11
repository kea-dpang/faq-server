package kea.dpang.faq.dto

data class PostCreateRequestDto(
    val categoryId: Int,
    val question: String,
    val answer: String
)