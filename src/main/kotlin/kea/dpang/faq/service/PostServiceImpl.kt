package kea.dpang.faq.service

import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.entity.Post
import kea.dpang.faq.exception.CategoryNotFoundException
import kea.dpang.faq.repository.CategoryRepository
import kea.dpang.faq.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository
) : PostService {

    @Transactional
    override fun createPost(userId: UUID, postCreateDto: PostCreateRequestDto): Post {
        // 카테고리를 먼저 찾고, 없다면 예외를 발생시킨다.
        val category = categoryRepository.findById(postCreateDto.categoryId)
            .orElseThrow { CategoryNotFoundException(postCreateDto.categoryId) }

        // 새로운 게시글을 생성한다.
        val post = Post(
            question = postCreateDto.question,
            answer = postCreateDto.answer,
            category = category,
            authorId = userId
        )

        // Post와 Category 간의 연관 관계를 설정한다.
        category.addPost(post)

        // Post를 저장하고 반환한다.
        return postRepository.save(post)
    }
}
