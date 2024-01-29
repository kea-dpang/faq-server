package kea.dpang.faq.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import kea.dpang.faq.exception.FAQNotFoundException
import kea.dpang.faq.repository.FAQRepository
import java.util.*
import kotlin.random.Random

class PostServiceImplUnitTest : BehaviorSpec({

    val mockFAQRepository = mockk<FAQRepository>()
    val postService = FAQServiceImpl(mockFAQRepository)

    Given("사용자가 특정 게시글을 조회하려고 할 때") {
        val userId = Random.nextLong()
        val category = Category.ETC
        val postId = 1L
        val FAQ = FAQ(
            question = "질문1",
            answer = "답변1",
            category = category,
            authorId = userId,
            id = postId
        )

        When("해당 id의 게시글이 존재할 때") {
            every { mockFAQRepository.findById(postId) } returns Optional.of(FAQ)

            Then("해당 id의 게시글이 반환되어야 한다") {
                val result = postService.getFAQById(postId)

                result.question shouldBe FAQ.question
                result.answer shouldBe FAQ.answer
                result.category.name shouldBe FAQ.category.name
                result.id shouldBe FAQ.id
            }
        }

        When("해당 id의 게시글이 존재하지 않을 때") {
            every { mockFAQRepository.findById(postId) } returns Optional.empty()

            Then("PostNotFoundException이 발생해야 한다") {
                shouldThrow<FAQNotFoundException> {
                    postService.getFAQById(postId)
                }
            }
        }
    }

    Given("사용자가 특정 게시글을 삭제하려고 할 때") {
        val postId = 1L
        val mockFAQ = mockk<FAQ>()

        When("해당 id의 게시글이 존재할 때") {
            every { mockFAQRepository.findById(postId) } returns Optional.of(mockFAQ)
            every { mockFAQRepository.delete(mockFAQ) } just Runs

            Then("해당 id의 게시글이 성공적으로 삭제되어야 한다") {
                postService.deleteFAQ(postId)
                verify(exactly = 1) { mockFAQRepository.delete(mockFAQ) }
            }
        }

        When("해당 id의 게시글이 존재하지 않을 때") {
            every { mockFAQRepository.findById(postId) } returns Optional.empty()

            Then("PostNotFoundException이 발생해야 한다") {
                shouldThrow<FAQNotFoundException> {
                    postService.deleteFAQ(postId)
                }
            }
        }
    }

})
