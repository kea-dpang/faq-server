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

    @GetMapping("/{postId}")
    fun readPost(
        @PathVariable postId: Long
    ): ResponseEntity<SuccessResponse<PostResponse>> {

        val post = postService.getPost(postId)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "글을 성공적으로 조회하였습니다.",
            data = post.toDto()
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @GetMapping("/category/{categoryId}")
    fun readPostsByCategory(
        @PathVariable categoryId: Int
    ): ResponseEntity<SuccessResponse<List<PostResponse>>> {

        val posts = postService.getPostsByCategory(categoryId)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "카테고리별 글을 성공적으로 조회하였습니다.",
            data = posts.map { it.toDto() }
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }


}