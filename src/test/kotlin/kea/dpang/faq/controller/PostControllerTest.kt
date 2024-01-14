package kea.dpang.faq.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.Post
import kea.dpang.faq.repository.PostRepository
import org.hamcrest.Matchers.`is`
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var postRepository: PostRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val clientId = UUID.randomUUID()

    private val postCreateDto = PostCreateRequestDto(
        question = "Kotest란 무엇인가요?",
        answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
        categoryName = Category.ETC.name
    )

    val postUpdateDto = PostUpdateRequestDto(
        question = "수정된 질문",
        answer = "수정된 답변",
        categoryName = Category.FAQ.name
    )

    private var postId = 1L

    @BeforeEach
    fun setup() {
        postRepository.deleteAll()

        val expectedPost = Post(
            question = postCreateDto.question,
            answer = postCreateDto.answer,
            category = Category.valueOf(postCreateDto.categoryName),
            authorId = UUID.randomUUID()

        ).apply {
            id = postId
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }

        // 생성한 expectedPost 객체를 데이터베이스에 저장 및 postId 갱신
        postId = postRepository.save(expectedPost).id!!
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 게시글 작성 요청을 보냈을 경우`() {
        mockMvc.perform(
            post("/api/posts")
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
            post("/api/posts")
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
            post("/api/posts")
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
            post("/api/posts")
                .with(csrf())
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 올바르지 않은 게시글 작성 요청을 보냈을 경우`() {
        val invalidPostCreateDto = PostCreateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            post("/api/posts")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPostCreateDto))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 올바르지 않은 게시글 작성 요청을 보냈을 경우`() {
        val invalidPostCreateDto = PostCreateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            post("/api/posts")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPostCreateDto))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 올바르지 않은 게시글 작성 요청을 보냈을 경우`() {
        val invalidPostCreateDto = PostCreateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            post("/api/posts")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPostCreateDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 올바르지 않은 게시글 작성 요청을 보냈을 경우`() {
        val invalidPostCreateDto = PostCreateRequestDto(
            question = "Kotest란 무엇인가요?",
            answer = "Kotest는 Kotlin용 유연하고 포괄적인 테스트 도구입니다.",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            post("/api/posts")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPostCreateDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하는 게시글을 조회 요청을 보냈을 경우`() {
        mockMvc.perform(
            get("/api/posts/$postId")
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
            get("/api/posts/$nonExistentPostId")
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
            get("/api/posts/$nonExistentPostId")
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
            get("/api/posts/$nonExistentPostId")
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
            get("/api/posts/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하는 카테고리로 게시글을 조회할 경우`() {
        val existentCategory = postCreateDto.categoryName

        mockMvc.perform(
            get("/api/posts/category/$existentCategory")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하는 카테고리로 게시글을 조회할 경우`() {
        val existentCategory = postCreateDto.categoryName
        val categoriesWithoutPosts = Category.entries.filter { it.name != existentCategory }

        // 카테고리1에 게시글이 1개인지 확인
        mockMvc.perform(
            get("/api/posts/category/$existentCategory")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()", `is`(1)))

        // 나머지 카테고리에 게시글이 없는지 확인
        for (category in categoriesWithoutPosts) {
            mockMvc.perform(
                get("/api/posts/category/$category")
                    .header("X-DPANG-CLIENT-ID", clientId)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data.length()", `is`(0)))
        }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하는 카테고리로 게시글을 조회할 경우`() {
        val existentCategory = postCreateDto.categoryName
        val categoriesWithoutPosts = Category.entries.filter { it.name != existentCategory }

        // 카테고리1에 게시글이 1개인지 확인
        mockMvc.perform(
            get("/api/posts/category/$existentCategory")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()", `is`(1)))

        // 나머지 카테고리에 게시글이 없는지 확인
        for (category in categoriesWithoutPosts) {
            mockMvc.perform(
                get("/api/posts/category/$category")
                    .header("X-DPANG-CLIENT-ID", clientId)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data.length()", `is`(0)))
        }
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하는 카테고리로 게시글을 조회할 경우`() {
        val existentCategory = postCreateDto.categoryName
        val categoriesWithoutPosts = Category.entries.filter { it.name != existentCategory }

        // 카테고리1에 게시글이 1개인지 확인
        mockMvc.perform(
            get("/api/posts/category/$existentCategory")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()", `is`(1)))

        // 나머지 카테고리에 게시글이 없는지 확인
        for (category in categoriesWithoutPosts) {
            mockMvc.perform(
                get("/api/posts/category/$category")
                    .header("X-DPANG-CLIENT-ID", clientId)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data.length()", `is`(0)))
        }
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/posts/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/posts/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/posts/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하지 않는 카테고리로 게시글을 조회할 경우`() {
        val nonExistentCategoryName = "존재하지 않는 카테고리"

        mockMvc.perform(
            get("/api/posts/category/$nonExistentCategoryName")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isUnauthorized)

        // 게시글이 수정되지 않았는지 확인
        val originalPost = postRepository.findById(postId).get()
        assertEquals(postCreateDto.question, originalPost.question)
        assertEquals(postCreateDto.answer, originalPost.answer)
        assertEquals(postCreateDto.categoryName, originalPost.category.name)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isForbidden)

        // 게시글이 수정되지 않았는지 확인
        val originalPost = postRepository.findById(postId).get()
        assertEquals(postCreateDto.question, originalPost.question)
        assertEquals(postCreateDto.answer, originalPost.answer)
        assertEquals(postCreateDto.categoryName, originalPost.category.name)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 수정되었는지 확인
        val updatedPost = postRepository.findById(postId).get()
        assertEquals(postUpdateDto.question, updatedPost.question)
        assertEquals(postUpdateDto.answer, updatedPost.answer)
        assertEquals(postUpdateDto.categoryName, updatedPost.category.name)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 올바른 정보로 게시글을 수정 요청을 보냈을 경우`() {
        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto))
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 수정되었는지 확인
        val updatedPost = postRepository.findById(postId).get()
        assertEquals(postUpdateDto.question, updatedPost.question)
        assertEquals(postUpdateDto.answer, updatedPost.answer)
        assertEquals(postUpdateDto.categoryName, updatedPost.category.name)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 올바르지 않은 정보로 게시글을 수정 요청을 보냈을 경우`() {
        // 원본 게시글 데이터
        val originalPost = postRepository.findById(postId).get()

        val updateDto = PostUpdateRequestDto(
            question = "수정된 질문",
            answer = "수정된 답변",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isUnauthorized)

        // 게시글이 수정되지 않았는지 확인
        val notUpdatedPost = postRepository.findById(postId).get()
        assertEquals(originalPost.question, notUpdatedPost.question)
        assertEquals(originalPost.answer, notUpdatedPost.answer)
        assertEquals(originalPost.category.name, notUpdatedPost.category.name)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 올바르지 않은 정보로 게시글을 수정 요청을 보냈을 경우`() {
        // 원본 게시글 데이터
        val originalPost = postRepository.findById(postId).get()

        val updateDto = PostUpdateRequestDto(
            question = "수정된 질문",
            answer = "수정된 답변",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isForbidden)

        // 게시글이 수정되지 않았는지 확인
        val notUpdatedPost = postRepository.findById(postId).get()
        assertEquals(originalPost.question, notUpdatedPost.question)
        assertEquals(originalPost.answer, notUpdatedPost.answer)
        assertEquals(originalPost.category.name, notUpdatedPost.category.name)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 올바르지 않은 정보로 게시글을 수정 요청을 보냈을 경우`() {
        // 원본 게시글 데이터
        val originalPost = postRepository.findById(postId).get()

        val updateDto = PostUpdateRequestDto(
            question = "수정된 질문",
            answer = "수정된 답변",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isNotFound)

        // 게시글이 수정되지 않았는지 확인
        val notUpdatedPost = postRepository.findById(postId).get()
        assertEquals(originalPost.question, notUpdatedPost.question)
        assertEquals(originalPost.answer, notUpdatedPost.answer)
        assertEquals(originalPost.category.name, notUpdatedPost.category.name)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 올바르지 않은 정보로 게시글을 수정 요청을 보냈을 경우`() {
        // 원본 게시글 데이터
        val originalPost = postRepository.findById(postId).get()

        val updateDto = PostUpdateRequestDto(
            question = "수정된 질문",
            answer = "수정된 답변",
            categoryName = "존재하지 않는 카테고리"
        )

        mockMvc.perform(
            put("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isNotFound)

        // 게시글이 수정되지 않았는지 확인
        val notUpdatedPost = postRepository.findById(postId).get()
        assertEquals(originalPost.question, notUpdatedPost.question)
        assertEquals(originalPost.answer, notUpdatedPost.answer)
        assertEquals(originalPost.category.name, notUpdatedPost.category.name)
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 게시글을 수정 요청을 보냈을 경우`() {
        val nonExistentPostId = -1L

        mockMvc.perform(
            put("/api/posts/$nonExistentPostId")
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
            put("/api/posts/$nonExistentPostId")
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
            put("/api/posts/$nonExistentPostId")
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
            put("/api/posts/$nonExistentPostId")
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
            delete("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)

        // 게시글이 정상적으로 존재하는지 확인
        assertTrue(postRepository.existsById(postId))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하는 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isForbidden)

        // 게시글이 정상적으로 존재하는지 확인
        assertTrue(postRepository.existsById(postId))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하는 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 삭제되었는지 확인
        assertFalse(postRepository.existsById(postId))
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하는 게시글을 삭제 요청을 보냈을 경우`() {
        mockMvc.perform(
            delete("/api/posts/$postId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isOk)

        // 게시글이 정상적으로 삭제되었는지 확인
        assertFalse(postRepository.existsById(postId))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `일반 사용자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/posts/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isForbidden)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(postRepository.existsById(nonExistentPostId))
    }

    @Test
    @WithAnonymousUser
    fun `인증되지 않은 사용자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/posts/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isUnauthorized)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(postRepository.existsById(nonExistentPostId))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `관리자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/posts/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(postRepository.existsById(nonExistentPostId))
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `최고 관리자가 존재하지 않는 게시글을 삭제 요청을 보냈을 경우`() {
        // 데이터베이스에 존재하지 않는 postId
        val nonExistentPostId = -1L

        mockMvc.perform(
            delete("/api/posts/$nonExistentPostId")
                .header("X-DPANG-CLIENT-ID", clientId)
        )
            .andExpect(status().isNotFound)

        // postId를 가진 게시글이 여전히 존재하지 않는 것을 확인
        assertFalse(postRepository.existsById(nonExistentPostId))
    }

}
