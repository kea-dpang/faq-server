package kea.dpang.faq.dto

data class PostCreateRequestDto(
    val categoryName: String,
    val question: String,
    val answer: String
)