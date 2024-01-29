package kea.dpang.faq.service

import kea.dpang.faq.dto.FAQCreateRequestDto
import kea.dpang.faq.dto.FAQUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.exception.FAQNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@SpringBootTest
@Transactional
class FAQServiceImplIntegrationTest {

    @Autowired
    private lateinit var postService: FAQServiceImpl

    @Test
    fun `게시글 생성 테스트`() {
        // Given: 새로운 게시글을 생성하기 위한 DTO로
        val userId = Random.nextLong()
        val postCreateDto = FAQCreateRequestDto(Category.FAQ, "질문", "답변")

        // When: 새로운 게시글을 생성했을 때
        val post = postService.createFAQ(userId, postCreateDto)

        // Then: 생성된 게시글은 null이 아니며, 입력한 값과 동일해야 한다.
        Assertions.assertNotNull(post)
        Assertions.assertEquals(postCreateDto.question, post.question)
        Assertions.assertEquals(postCreateDto.answer, post.answer)
        Assertions.assertEquals(postCreateDto.category, post.category)
        Assertions.assertEquals(userId, post.authorId)

        // Then: 생성된 게시글을 ID로 검색했을 때, 검색된 게시글의 값이 동일해야 한다.
        val foundPost = postService.getFAQById(post.id!!)
        Assertions.assertEquals(post.question, foundPost.question)
        Assertions.assertEquals(post.answer, foundPost.answer)
        Assertions.assertEquals(post.category, foundPost.category)
        Assertions.assertEquals(post.authorId, foundPost.authorId)
    }

    @Test
    fun `특정 카테고리 게시글 검색 테스트`() {
        // Given: FAQ 카테고리, Notice 카테고리에 각각 한 개의 게시글이 존재할 때
        val userId = Random.nextLong()
        val postCreateDto1 = FAQCreateRequestDto(Category.FAQ, "질문1", "답변1")
        val postCreateDto2 = FAQCreateRequestDto(Category.MEMBER, "질문2", "답변2")
        postService.createFAQ(userId, postCreateDto1)
        postService.createFAQ(userId, postCreateDto2)

        // When: FAQ 카테고리에 속하는 게시글을 검색하면
        val postsInFaq = postService.getFAQsByCategory(Category.FAQ)

        // Then: 검색된 게시글이 null이 아니며, 검색된 게시글의 카테고리가 모두 FAQ여야 한다
        Assertions.assertNotNull(postsInFaq)
        postsInFaq.forEach {
            Assertions.assertEquals(Category.FAQ, it.category)
            Assertions.assertNotEquals(Category.SHIPPING, it.category)
            Assertions.assertNotEquals(Category.CANCELLATION_REFUND_EXCHANGE, it.category)
            Assertions.assertNotEquals(Category.PAYMENT, it.category)
            Assertions.assertNotEquals(Category.MEMBER, it.category)
            Assertions.assertNotEquals(Category.ETC, it.category)
        }
    }

    @Test
    fun `게시글 수정 테스트`() {
        // Given: 게시글을
        val userId = Random.nextLong()
        val postCreateDto = FAQCreateRequestDto(Category.FAQ, "질문", "답변")
        val createdPost = postService.createFAQ(userId, postCreateDto)
        val postUpdateDto = FAQUpdateRequestDto(Category.FAQ, "수정된 질문", "수정된 답변")

        // When: 수정했을 때
        val updatedPost = postService.updateFAQ(userId, createdPost.id!!, postUpdateDto)

        // Then: 수정된 게시글은 null이 아니며, 수정한 값과 같아야 한다.
        Assertions.assertNotNull(updatedPost)
        Assertions.assertEquals(postUpdateDto.question, updatedPost.question)
        Assertions.assertEquals(postUpdateDto.answer, updatedPost.answer)
        Assertions.assertEquals(postUpdateDto.category, updatedPost.category)

        // Then: 수정된 게시글을 ID로 검색했을 때 검색된 게시글의 값이 같아야 한다.
        val foundPost = postService.getFAQById(updatedPost.id!!)
        Assertions.assertEquals(updatedPost.question, foundPost.question)
        Assertions.assertEquals(updatedPost.answer, foundPost.answer)
        Assertions.assertEquals(updatedPost.category, foundPost.category)
    }

    @Test
    fun `게시글 삭제 테스트`() {
        // Given: 게시글을
        val userId = Random.nextLong()
        val postCreateDto = FAQCreateRequestDto(Category.FAQ, "질문", "답변")
        val createdPost = postService.createFAQ(userId, postCreateDto)

        // When: 삭제했을 때
        postService.deleteFAQ(createdPost.id!!)

        // Then: 삭제한 게시글을 다시 검색하려고 하면, PostNotFoundException이 발생해야 한다.
        Assertions.assertThrows(FAQNotFoundException::class.java) {
            postService.getFAQById(createdPost.id!!)
        }
    }

}
