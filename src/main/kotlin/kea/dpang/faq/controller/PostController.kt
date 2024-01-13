package kea.dpang.faq.controller

import kea.dpang.faq.base.BaseResponse
import kea.dpang.faq.base.SuccessResponse
import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostResponse
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/posts")
class PostController(private val postService: PostService) {

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping("/{postId}")
    fun readPost(
        @PathVariable postId: Long
    ): ResponseEntity<SuccessResponse<PostResponse>> {

        val post = postService.getPostById(postId)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "글을 성공적으로 조회하였습니다.",
            data = post.toDto()
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping("/category/{categoryName}")
    fun readPostsByCategory(
        @PathVariable categoryName: String
    ): ResponseEntity<SuccessResponse<List<PostResponse>>> {

        val posts = postService.getPostsByCategoryName(categoryName.uppercase())

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "카테고리별 글을 성공적으로 조회하였습니다.",
            data = posts.map { it.toDto() }
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{postId}")
    fun updatePost(
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: UUID,
        @PathVariable postId: Long,
        @RequestBody faqUpdateDto: PostUpdateRequestDto
    ): ResponseEntity<SuccessResponse<PostResponse>> {

        val updatedPost = postService.updatePost(clientId, postId, faqUpdateDto)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "글을 성공적으로 수정되었습니다.",
            data = updatedPost.toDto()
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long
    ): ResponseEntity<BaseResponse> {

        postService.deletePost(postId)

        // 응답 성공 객체 생성
        val successResponse = BaseResponse(
            status = HttpStatus.OK.value(),
            message = "글을 성공적으로 삭제하였습니다."
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

}