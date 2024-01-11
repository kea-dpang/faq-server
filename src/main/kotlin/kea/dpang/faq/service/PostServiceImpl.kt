package kea.dpang.faq.service

import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.entity.Post
import kea.dpang.faq.exception.CategoryNotFoundException
import kea.dpang.faq.exception.PostNotFoundException
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

    override fun getPost(postId: Long): Post {
        return postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }
    }

    override fun getPostsByCategory(categoryId: Int): List<Post> {
        return postRepository.findByCategoryId(categoryId)
    }

    override fun updatePost(clientId: UUID, postId: Long, postUpdateDto: PostUpdateRequestDto): Post {
        // 게시글 조회. 없는 경우 예외 발생
        val post = postRepository.findById(postId)
            .orElseThrow { PostNotFoundException(postId) }

        // 수정할 카테고리 조회. 없는 경우 예외 발생
        val updatedCategoryId = postUpdateDto.categoryId
        val updatedCategory = categoryRepository.findById(updatedCategoryId)
            .orElseThrow { CategoryNotFoundException(updatedCategoryId) }

        // 해당 요청은 관리자 전용 요청으로, 기존 작성자와 같지 않더라도 게시글 수정 요청을 할 수 있음
        //  -> 사용자 비교 X

        // 게시글 내용 업데이트
        post.question = postUpdateDto.question
        post.answer = postUpdateDto.answer
        post.modifyCategory(updatedCategory)

        return postRepository.save(post)
    }

    override fun deletePost(postId: Long) {
        // 게시글 조회. 없는 경우 예외 발생
        val post = postRepository.findById(postId)
            .orElseThrow { PostNotFoundException(postId) }

        // 해당 요청은 관리자 전용 요청으로, 기존 작성자와 같지 않더라도 게시글 삭제 요청을 할 수 있음
        //  -> 사용자 비교 X

        // 게시글 삭제
        postRepository.delete(post)
    }

}