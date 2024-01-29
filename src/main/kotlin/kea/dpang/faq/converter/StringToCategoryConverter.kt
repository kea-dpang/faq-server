package kea.dpang.faq.converter

import kea.dpang.faq.entity.Category
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToCategoryConverter : Converter<String, Category> {
    override fun convert(source: String): Category {
        return Category.fromString(source)
    }
}