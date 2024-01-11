package kea.dpang.faq.service

import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.entity.Post
import java.util.UUID

fun interface PostService {

    fun createPost(userId: UUID, postCreateDto: PostCreateRequestDto): Post
}