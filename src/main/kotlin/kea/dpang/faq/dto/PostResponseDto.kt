package kea.dpang.faq.dto

import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.Post
import java.time.LocalDateTime
import java.util.*

data class PostResponseDto(
    val postId: Long,
    val question: String,
    val answer: String,
    val category: Category,
    val authorId: UUID,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(post: Post): PostResponseDto {
            return PostResponseDto(
                postId = post.id!!,
                question = post.question,
                answer = post.answer,
                category = post.category,
                authorId = post.authorId,
                createdAt = post.createdAt!!
            )
        }
    }
}