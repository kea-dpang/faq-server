package kea.dpang.faq.service

import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.Post
import java.util.*

interface PostService {

    fun createPost(userId: UUID, postCreateDto: PostCreateRequestDto): Post

    fun getPostById(postId: Long): Post

    fun getPostsByCategoryName(category: Category): List<Post>

    fun updatePost(clientId: UUID, postId: Long, postUpdateDto: PostUpdateRequestDto): Post

    fun deletePost(postId: Long)

}