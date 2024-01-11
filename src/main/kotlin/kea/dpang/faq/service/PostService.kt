package kea.dpang.faq.service

import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.entity.Post
import java.util.UUID

interface PostService {

    fun createPost(userId: UUID, postCreateDto: PostCreateRequestDto): Post

    fun getPost(postId: Long): Post

    fun getPostsByCategory(categoryId: Int): List<Post>

    fun updatePost(clientId: UUID, postId: Long, postUpdateDto: PostUpdateRequestDto): Post

}