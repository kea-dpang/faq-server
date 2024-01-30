package kea.dpang.faq.service

import kea.dpang.faq.dto.CreateFAQRequestDto
import kea.dpang.faq.dto.UpdateFAQRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FAQService {

    fun createFAQ(userId: Long, createFAQRequestDto: CreateFAQRequestDto): FAQ

    fun getAllFAQs(pageable: Pageable): Page<FAQ>

    fun getFAQById(faqId: Long): FAQ

    fun getFAQsByCategory(category: Category, pageable: Pageable): Page<FAQ>

    fun updateFAQ(clientId: Long, faqId: Long, updateFAQRequestDto: UpdateFAQRequestDto): FAQ

    fun deleteFAQ(faqId: Long)

    fun deleteFAQs(faqIds: List<Long>)

}