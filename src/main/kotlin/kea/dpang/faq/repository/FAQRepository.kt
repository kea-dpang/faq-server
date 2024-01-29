package kea.dpang.faq.repository

import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FAQRepository : JpaRepository<FAQ, Long> {

    fun findByCategory(category: Category, pageable: Pageable): Page<FAQ>
}