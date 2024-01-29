package kea.dpang.faq.service

import kea.dpang.faq.dto.FAQCreateRequestDto
import kea.dpang.faq.dto.FAQUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface FAQService {

    fun createFAQ(userId: UUID, faqCreateRequestDto: FAQCreateRequestDto): FAQ

    fun getAllFAQs(pageable: Pageable): Page<FAQ>

    fun getFAQById(faqId: Long): FAQ

    fun getFAQsByCategory(category: Category): List<FAQ>

    fun updateFAQ(clientId: UUID, faqId: Long, faqUpdateRequestDto: FAQUpdateRequestDto): FAQ

    fun deleteFAQ(faqId: Long)

}