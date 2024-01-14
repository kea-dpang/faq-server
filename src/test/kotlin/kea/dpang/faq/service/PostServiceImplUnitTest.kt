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
import kea.dpang.faq.repository.PostRepository
import java.util.*

class PostServiceImplUnitTest : BehaviorSpec({

    val mockPostRepository = mockk<PostRepository>()
    val postService = PostServiceImpl(mockPostRepository)

    Given("사용자가 게시글을 생성하려고 할 때") {
        val userId = UUID.randomUUID()
        val postCreateDto = PostCreateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryName = Category.ETC.name
        )

        every { mockPostRepository.save(any()) } answers { firstArg() }

        When("유효한 카테고리 이름을 소문자로 입력했을 때") {
            val result = postService.createPost(
                userId,
                postCreateDto.copy(categoryName = postCreateDto.categoryName.lowercase())
            )

            Then("게시글이 성공적으로 생성되어야 한다") {
                result.question shouldBe postCreateDto.question
                result.answer shouldBe postCreateDto.answer
                result.category.name shouldBe postCreateDto.categoryName.uppercase() // category는 Enum type이므로, 이름을 비교
                result.authorId shouldBe userId
            }
        }

        When("유효한 카테고리 이름을 대문자로 입력했을 때") {
            val result = postService.createPost(
                userId,
                postCreateDto.copy(categoryName = postCreateDto.categoryName.uppercase())
            )

            Then("게시글이 성공적으로 생성되어야 한다") {
                result.question shouldBe postCreateDto.question
                result.answer shouldBe postCreateDto.answer
                result.category.name shouldBe postCreateDto.categoryName.uppercase()
                result.authorId shouldBe userId
            }
        }

        When("유효한 카테고리 이름이 대소문자가 섞여있을 때") {
            val result = postService.createPost(
                userId = userId,
                postCreateDto = postCreateDto.copy(
                    categoryName = postCreateDto.categoryName
                        .lowercase()
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                            else it.toString()
                        }
                )
            )

            Then("게시글이 성공적으로 생성되어야 한다") {
                result.question shouldBe postCreateDto.question
                result.answer shouldBe postCreateDto.answer
                result.category.name shouldBe postCreateDto.categoryName.uppercase()
                result.authorId shouldBe userId
            }
        }

        When("존재하지 않는 카테고리 이름을 입력했을 때") {
            val invalidPostCreateDto = postCreateDto.copy(categoryName = "INVALID_CATEGORY") // 존재하지 않는 카테고리

            Then("CategoryNotFoundException이 발생해야 한다") {
                shouldThrow<CategoryNotFoundException> {
                    postService.createPost(userId, invalidPostCreateDto)
                }
            }
        }
    }

    Given("사용자가 특정 카테고리의 모든 게시글을 조회하려고 할 때") {
        val userId = UUID.randomUUID()
        val category = Category.ETC
        val posts = listOf(
            Post(question = "질문1", answer = "답변1", category = category, authorId = userId),
            Post(question = "질문2", answer = "답변2", category = category, authorId = userId)
        )

        When("카테고리에 속하는 게시글이 존재하고, 카테고리 이름을 모두 소문자로 입력했을 때") {
            every { mockPostRepository.findByCategory(any()) } returns posts
            val result = postService.getPostsByCategoryName(category.name.lowercase())

            Then("해당 카테고리의 모든 게시글이 반환되어야 한다") {
                result.size shouldBe posts.size
                result[0].question shouldBe posts[0].question
                result[0].answer shouldBe posts[0].answer
                result[1].question shouldBe posts[1].question
                result[1].answer shouldBe posts[1].answer
            }
        }

        When("카테고리에 속하는 게시글이 존재하고, 카테고리 이름을 모두 대문자로 입력했을 때") {
            every { mockPostRepository.findByCategory(any()) } returns posts
            val result = postService.getPostsByCategoryName(category.name.uppercase())

            Then("해당 카테고리의 모든 게시글이 반환되어야 한다") {
                result.size shouldBe posts.size
                result[0].question shouldBe posts[0].question
                result[0].answer shouldBe posts[0].answer
                result[1].question shouldBe posts[1].question
                result[1].answer shouldBe posts[1].answer
            }
        }

        When("카테고리에 속하는 게시글이 존재하고, 카테고리 이름이 대소문자가 섞여있을 때") {
            every { mockPostRepository.findByCategory(any()) } returns posts

            val result = postService.getPostsByCategoryName(category.name
                .lowercase()
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
            )

            Then("해당 카테고리의 모든 게시글이 반환되어야 한다") {
                result.size shouldBe posts.size
                result[0].question shouldBe posts[0].question
                result[0].answer shouldBe posts[0].answer
                result[1].question shouldBe posts[1].question
                result[1].answer shouldBe posts[1].answer
            }
        }

        When("카테고리에 속하는 게시글이 존재하지 않을 때") {
            every { mockPostRepository.findByCategory(any()) } returns emptyList()
            val result = postService.getPostsByCategoryName(category.name.lowercase())

            Then("빈 리스트가 반환되어야 한다") {
                result.size shouldBe 0
            }
        }

        When("존재하지 않는 카테고리 이름을 입력했을 때") {
            val invalidCategoryName = "invalid"
            every { mockPostRepository.findByCategory(any()) } returns posts

            Then("CategoryNotFoundException이 발생해야 한다") {
                shouldThrow<CategoryNotFoundException> {
                    postService.getPostsByCategoryName(invalidCategoryName)
                }
            }
        }
    }

    Given("사용자가 특정 게시글을 조회하려고 할 때") {
        val userId = UUID.randomUUID()
        val category = Category.ETC
        val postId = 1L
        val post = Post(
            question = "질문1",
            answer = "답변1",
            category = category,
            authorId = userId,
            id = postId
        )

        When("해당 id의 게시글이 존재할 때") {
            every { mockPostRepository.findById(postId) } returns Optional.of(post)

            Then("해당 id의 게시글이 반환되어야 한다") {
                val result = postService.getPostById(postId)

                result.question shouldBe post.question
                result.answer shouldBe post.answer
                result.category.name shouldBe post.category.name
                result.id shouldBe post.id
            }
        }

        When("해당 id의 게시글이 존재하지 않을 때") {
            every { mockPostRepository.findById(postId) } returns Optional.empty()

            Then("PostNotFoundException이 발생해야 한다") {
                shouldThrow<PostNotFoundException> {
                    postService.getPostById(postId)
                }
            }
        }
    }

    Given("사용자가 게시글을 수정하려고 할 때") {
        val clientId = UUID.randomUUID()
        val postId = 1L
        val postUpdateDto = PostUpdateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryName = Category.ETC.name
        )

        val originalPost = Post(
            question = "원래 질문",
            answer = "원래 답변",
            category = Category.ETC,
            authorId = clientId,
            id = postId,
        )

        every { mockPostRepository.findById(any()) } returns Optional.of(originalPost)
        every { mockPostRepository.save(any()) } answers { firstArg() }

        When("유효한 카테고리 이름을 소문자로 입력했을 때") {
            val result = postService.updatePost(
                clientId,
                postId,
                postUpdateDto.copy(categoryName = postUpdateDto.categoryName.lowercase())
            )

            Then("게시글이 성공적으로 수정되어야 한다") {
                result.question shouldBe postUpdateDto.question
                result.answer shouldBe postUpdateDto.answer
                result.category.name shouldBe postUpdateDto.categoryName.uppercase() // category는 Enum type이므로, 이름을 비교
            }
        }

        When("유효한 카테고리 이름을 대문자로 입력했을 때") {
            val result = postService.updatePost(
                clientId,
                postId,
                postUpdateDto.copy(categoryName = postUpdateDto.categoryName.uppercase())
            )

            Then("게시글이 성공적으로 수정되어야 한다") {
                result.question shouldBe postUpdateDto.question
                result.answer shouldBe postUpdateDto.answer
                result.category.name shouldBe postUpdateDto.categoryName.uppercase() // category는 Enum type이므로, 이름을 비교
            }
        }

        When("유효한 카테고리 이름이 대소문자가 섞여있을 때") {
            val result = postService.updatePost(
                clientId = clientId,
                postId = postId,
                postUpdateDto = postUpdateDto.copy(
                    categoryName = postUpdateDto.categoryName
                        .lowercase()
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                            else it.toString()
                        }
                )
            )

            Then("게시글이 성공적으로 수정되어야 한다") {
                result.question shouldBe postUpdateDto.question
                result.answer shouldBe postUpdateDto.answer
                result.category.name shouldBe postUpdateDto.categoryName.uppercase() // category는 Enum type이므로, 이름을 비교
            }
        }

        When("존재하지 않는 카테고리 이름을 입력했을 때") {
            val invalidPostUpdateDto = postUpdateDto.copy(categoryName = "INVALID_CATEGORY") // 존재하지 않는 카테고리

            Then("CategoryNotFoundException이 발생해야 한다") {
                shouldThrow<CategoryNotFoundException> {
                    postService.updatePost(clientId, postId, invalidPostUpdateDto)
                }
            }
        }
    }

    Given("사용자가 특정 게시글을 삭제하려고 할 때") {
        val postId = 1L
        val mockPost = mockk<Post>()

        When("해당 id의 게시글이 존재할 때") {
            every { mockPostRepository.findById(postId) } returns Optional.of(mockPost)
            every { mockPostRepository.delete(mockPost) } just Runs

            Then("해당 id의 게시글이 성공적으로 삭제되어야 한다") {
                postService.deletePost(postId)
                verify(exactly = 1) { mockPostRepository.delete(mockPost) }
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
