package kea.dpang.faq.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.exception.CategoryNotFoundException
import kea.dpang.faq.repository.CategoryRepository
import kea.dpang.faq.repository.PostRepository
import java.util.*

class PostServiceImplUnitTest : BehaviorSpec({

    val mockPostRepository = mockk<PostRepository>()
    val mockCategoryRepository = mockk<CategoryRepository>()
    val postService = PostServiceImpl(mockPostRepository, mockCategoryRepository)

    Given("사용자가 게시글을 생성하려고 할 때") {
        val userId = UUID.randomUUID()
        val postCreateDto = PostCreateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryId = 1
        )
        val category = Category("테스트", mutableListOf(), 1)

        every { mockCategoryRepository.findById(any()) } returns Optional.of(category)
        every { mockPostRepository.save(any()) } answers { firstArg() }

        When("유효한 categoryId와 postCreateDto가 주어졌을 때") {
            val result = postService.createPost(userId, postCreateDto)

            Then("게시글이 성공적으로 생성되어야 한다") {
                result.question shouldBe postCreateDto.question
                result.answer shouldBe postCreateDto.answer
                result.category shouldBe category
                result.authorId shouldBe userId
            }
        }

        When("존재하지 않는 categoryId가 주어졌을 때") {
            every { mockCategoryRepository.findById(any()) } returns Optional.empty()

            Then("CategoryNotFoundException이 발생해야 한다") {
                try {
                    postService.createPost(userId, postCreateDto)
                } catch (e: Exception) {
                    e shouldBe CategoryNotFoundException(postCreateDto.categoryId)
                }
            }
        }
    }
})
