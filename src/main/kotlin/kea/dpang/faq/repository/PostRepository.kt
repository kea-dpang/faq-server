package kea.dpang.faq.repository

import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long> {

    fun findByCategory(category: Category): List<Post>
}