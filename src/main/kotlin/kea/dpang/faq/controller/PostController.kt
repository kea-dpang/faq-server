package kea.dpang.faq.controller

import kea.dpang.faq.base.SuccessResponse
import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostResponse
import kea.dpang.faq.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/posts")
class PostController(private val postService: PostService) {

    @PostMapping
    fun createPost(
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: UUID,
        @RequestBody postCreateDto: PostCreateRequestDto
    ): ResponseEntity<SuccessResponse<PostResponse>> {

        val createdPost = postService.createPost(clientId, postCreateDto)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.CREATED.value(),
            message = "글이 성공적으로 작성되었습니다.",
            data = createdPost.toDto()
        )

        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }


}