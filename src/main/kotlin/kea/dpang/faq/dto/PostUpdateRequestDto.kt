package kea.dpang.faq.dto

data class PostUpdateRequestDto(
    val categoryName: String,
    val question: String,
    val answer: String
)