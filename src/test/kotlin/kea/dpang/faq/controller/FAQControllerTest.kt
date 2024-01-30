package kea.dpang.faq.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kea.dpang.faq.dto.CreateFAQRequestDto
import kea.dpang.faq.dto.UpdateFAQRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import kea.dpang.faq.repository.FAQRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class FAQControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var faqRepository: FAQRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val clientId = Random.nextLong()

    private val postCreateDto = CreateFAQRequestDto(
        question = "Kotest란 무엇인가요?",
        answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
        category = Category.ETC
    )

    val postUpdateDto = UpdateFAQRequestDto(
        question = "수정된 질문",
        answer = "수정된 답변",
        category = Category.FAQ
    )

    private var faqId = 1L

    @BeforeEach
    fun setup() {
        faqRepository.deleteAll()

        val expectedFAQ = FAQ(
            question = postCreateDto.question,
            answer = postCreateDto.answer,
            category = postCreateDto.category,
            authorId = Random.nextLong()

        ).apply {
            id = faqId
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }

        // 생성한 expectedPost 객체를 데이터베이스에 저장 및 postId 갱신
        faqId = faqRepository.save(expectedFAQ).id!!
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 게시글 작성 요청을 보냈을 경우`() {
        mockMvc.perform(
            post("/api/faq")
                .with(csrf())
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 게시글 작성 요청을 보냈을 경우`() {
        mockMvc.perform(
            post("/api/faq")
                .with(csrf())
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 게시글 작성 요청을 보냈을 경우`() {
        mockMvc.perform(
            post("/api/faq")
                .with(csrf())
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 게시글 작성 요청을 보냈을 경우`() {
        mockMvc.perform(
            post("/api/faq")
                .with(csrf())
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 게시글을 조회 요청을 보냈을 경우`() {
        // 존재하지 않는 게시글의 ID 설정
        val nonExistentPostId = -1L

        mockMvc.perform(
            get("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하지 않는 게시글을 조회 요청을 보냈을 경우`() {
        // 존재하지 않는 게시글의 ID 설정
        val nonExistentPostId = -1L

        mockMvc.perform(
            get("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하지 않는 게시글을 조회 요청을 보냈을 경우`() {
        // 존재하지 않는 게시글의 ID 설정
        val nonExistentPostId = -1L

        mockMvc.perform(
            get("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하지 않는 게시글을 조회 요청을 보냈을 경우`() {
        // 존재하지 않는 게시글의 ID 설정
        val nonExistentPostId = -1L

        mockMvc.perform(
            get("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/faq/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/faq/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/faq/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/faq/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isUnauthorized)

        // 게시글이 수정되지 않았는지 확인
        val originalPost = faqRepository.findById(faqId).get()
        assertEquals(postCreateDto.question, originalPost.question)
        assertEquals(postCreateDto.answer, originalPost.answer)
        assertEquals(postCreateDto.category, originalPost.category)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isForbidden)

        // 게시글이 수정되지 않았는지 확인
        val originalPost = faqRepository.findById(faqId).get()
        assertEquals(postCreateDto.question, originalPost.question)
        assertEquals(postCreateDto.answer, originalPost.answer)
        assertEquals(postCreateDto.category, originalPost.category)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 수정되었는지 확인
        val updatedPost = faqRepository.findById(faqId).get()
        assertEquals(postUpdateDto.question, updatedPost.question)
        assertEquals(postUpdateDto.answer, updatedPost.answer)
        assertEquals(postUpdateDto.category, updatedPost.category)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 수정되었는지 확인
        val updatedPost = faqRepository.findById(faqId).get()
        assertEquals(postUpdateDto.question, updatedPost.question)
        assertEquals(postUpdateDto.answer, updatedPost.answer)
        assertEquals(postUpdateDto.category, updatedPost.category)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 게시글을 수정 요청을 보냈을 경우`() {
        val nonExistentPostId = -1L

        mockMvc.perform(
            put("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하지 않는 게시글을 수정 요청을 보냈을 경우`() {
        val nonExistentPostId = -1L

        mockMvc.perform(
            put("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하지 않는 게시글을 수정 요청을 보냈을 경우`() {
        val nonExistentPostId = -1L

        mockMvc.perform(
            put("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하지 않는 게시글을 수정 요청을 보냈을 경우`() {
        val nonExistentPostId = -1L

        mockMvc.perform(
            put("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)

        // 게시글이 정상적으로 존재하는지 확인
        assertTrue(faqRepository.existsById(faqId))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하는 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isForbidden)

        // 게시글이 정상적으로 존재하는지 확인
        assertTrue(faqRepository.existsById(faqId))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하는 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 삭제되었는지 확인
        assertFalse(faqRepository.existsById(faqId))
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하는 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/faq/$faqId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 삭제되었는지 확인
        assertFalse(faqRepository.existsById(faqId))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isForbidden)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(faqRepository.existsById(nonExistentPostId))
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(faqRepository.existsById(nonExistentPostId))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(faqRepository.existsById(nonExistentPostId))
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/faq/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(faqRepository.existsById(nonExistentPostId))
    }

}
