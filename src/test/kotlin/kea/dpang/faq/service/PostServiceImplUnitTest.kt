package kea.dpang.faq.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.Post
import kea.dpang.faq.exception.CategoryNotFoundException
import kea.dpang.faq.exception.PostNotFoundException
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

    Given("사용자가 특정 카테고리의 모든 게시글을 조회하려고 할 때") {
        val userId = UUID.randomUUID()
        val categoryId = 1
        val category = Category("테스트", mutableListOf(), categoryId)
        val posts = listOf(
            Post(question = "질문1", answer = "답변1", category = category, authorId = userId),
            Post(question = "질문2", answer = "답변2", category = category, authorId = userId)
        )

        every { mockPostRepository.findByCategoryId(categoryId) } returns posts

        When("카테고리에 속하는 게시글이 존재할 때") {
            val result = postService.getPostsByCategory(categoryId)

            Then("해당 카테고리의 모든 게시글이 반환되어야 한다") {
                result.size shouldBe posts.size
                result[0].question shouldBe posts[0].question
                result[0].answer shouldBe posts[0].answer
                result[1].question shouldBe posts[1].question
                result[1].answer shouldBe posts[1].answer
            }
        }

        When("카테고리에 속하는 게시글이 존재하지 않을 때") {
            every { mockPostRepository.findByCategoryId(categoryId) } returns emptyList()

            Then("빈 리스트가 반환되어야 한다") {
                val result = postService.getPostsByCategory(categoryId)
                result.size shouldBe 0
            }
        }
    }

    Given("사용자가 특정 게시글을 조회하려고 할 때") {
        val userId = UUID.randomUUID()
        val categoryId = 1
        val category = Category("테스트", mutableListOf(), categoryId)
        val postId = 1L
        val post = Post(question = "질문1", answer = "답변1", category = category, authorId = userId, postId = postId)

        When("해당 id의 게시글이 존재할 때") {
            every { mockPostRepository.findById(postId) } returns Optional.of(post)

            Then("해당 id의 게시글이 반환되어야 한다") {
                val result = postService.getPost(postId)
                result.question shouldBe post.question
                result.answer shouldBe post.answer
            }
        }

        When("해당 id의 게시글이 존재하지 않을 때") {
            every { mockPostRepository.findById(postId) } returns Optional.empty()

            Then("PostNotFoundException이 발생해야 한다") {
                shouldThrow<PostNotFoundException> {
                    postService.getPost(postId)
                }
            }
        }
    }

    Given("사용자가 특정 게시글을 수정하려고 할 때") {
        val userId = UUID.randomUUID()
        val categoryId = 1
        val category = Category("테스트", mutableListOf(), categoryId)
        val postId = 1L
        val post = Post(question = "질문1", answer = "답변1", category = category, authorId = userId, postId = postId)
        val updateRequestDto = PostUpdateRequestDto(
            question = "수정된 질문",
            answer = "수정된 답변",
            categoryId = categoryId
        )

        every { mockPostRepository.findById(any()) } returns Optional.of(post)
        every { mockCategoryRepository.findById(any()) } returns Optional.of(category)
        every { mockPostRepository.save(any()) } answers { firstArg() }

        When("유효한 postId와 postUpdateDto가 주어졌을 때") {
            val result = postService.updatePost(userId, postId, updateRequestDto)

            Then("게시글이 성공적으로 수정되어야 한다") {
                result.question shouldBe updateRequestDto.question
                result.answer shouldBe updateRequestDto.answer
                result.category shouldBe category
                result.authorId shouldBe userId
            }
        }

        When("존재하지 않는 postId가 주어졌을 때") {
            every { mockPostRepository.findById(any()) } returns Optional.empty()

            Then("PostNotFoundException이 발생해야 한다") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(userId, postId, updateRequestDto)
                }
            }
        }

        When("존재하지 않는 categoryId가 주어졌을 때") {
            every { mockPostRepository.findById(any()) } returns Optional.of(post)
            every { mockCategoryRepository.findById(any()) } returns Optional.empty()

            Then("CategoryNotFoundException이 발생해야 한다") {
                shouldThrow<CategoryNotFoundException> {
                    postService.updatePost(userId, postId, updateRequestDto)
                }
            }
        }
    }

    Given("사용자가 특정 게시글을 삭제하려고 할 때") {
        val userId = UUID.randomUUID()
        val categoryId = 1
        val category = Category("테스트", mutableListOf(), categoryId)
        val postId = 1L
        val post = Post(question = "질문1", answer = "답변1", category = category, authorId = userId, postId = postId)

        When("해당 id의 게시글이 존재할 때") {
            every { mockPostRepository.findById(postId) } returns Optional.of(post)
            every { mockPostRepository.delete(post) } just Runs

            Then("해당 id의 게시글이 성공적으로 삭제되어야 한다") {
                postService.deletePost(postId)
                verify(exactly = 1) { mockPostRepository.delete(post) }
            }
        }

        When("해당 id의 게시글이 존재하지 않을 때") {
            every { mockPostRepository.findById(postId) } returns Optional.empty()

            Then("PostNotFoundException이 발생해야 한다") {
                shouldThrow<PostNotFoundException> {
                    postService.deletePost(postId)
                }
            }
        }
    }

})
