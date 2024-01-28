package kea.dpang.faq.dto

import kea.dpang.faq.entity.Category

data class PostCreateRequestDto(
    val category: Category,
    val question: String,
    val answer: String
)