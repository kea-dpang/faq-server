package kea.dpang.faq.dto

import jakarta.persistence.Convert
import kea.dpang.faq.converter.StringToCategoryConverter
import kea.dpang.faq.entity.Category

data class FAQUpdateRequestDto(
    @Convert(converter = StringToCategoryConverter::class)
    val category: Category,
    val question: String,
    val answer: String
)