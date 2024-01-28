package kea.dpang.faq.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.faq.base.BaseResponse
import kea.dpang.faq.base.SuccessResponse
import kea.dpang.faq.dto.PostCreateRequestDto
import kea.dpang.faq.dto.PostResponseDto
import kea.dpang.faq.dto.PostUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "FAQ")
@RequestMapping("/api/faq")
class PostController(private val postService: PostService) {

    @Operation(summary = "게시글 작성", description = "관리자 권한을 가진 사용자가 새로운 게시글을 생성합니다.")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping
    fun createPost(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: UUID,
        @Parameter(description = "포스트 생성 정보")
        @RequestBody postCreateDto: PostCreateRequestDto
    ): ResponseEntity<SuccessResponse<PostResponseDto>> {

        val createdPost = postService.createPost(clientId, postCreateDto)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.CREATED.value(),
            message = "글이 성공적으로 작성되었습니다.",
            data = PostResponseDto.from(createdPost)
        )

        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }

    @Operation(summary = "게시글 조회", description = "특정 게시글을 조회합니다.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping("/{postId}")
    fun readPost(
        @Parameter(description = "조회할 포스트의 ID")
        @PathVariable postId: Long
    ): ResponseEntity<SuccessResponse<PostResponseDto>> {

        val post = postService.getPostById(postId)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "글을 성공적으로 조회하였습니다.",
            data = PostResponseDto.from(post)
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Operation(summary = "카테고리별 게시글 조회", description = "특정 카테고리의 게시글을 조회합니다.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping("/category/{category}")
    fun readPostsByCategory(
        @Parameter(description = "조회할 카테고리")
        @PathVariable category: Category
    ): ResponseEntity<SuccessResponse<List<PostResponseDto>>> {

        val posts = postService.getPostsByCategoryName(category)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "카테고리별 글을 성공적으로 조회하였습니다.",
            data = posts.map { PostResponseDto.from(it) }
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Operation(summary = "게시글 수정", description = "관리자 권한을 가진 사용자가 특정 게시글을 수정합니다.")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{postId}")
    fun updatePost(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: UUID,
        @Parameter(description = "수정할 포스트의 ID")
        @PathVariable postId: Long,
        @Parameter(description = "포스트 수정 정보")
        @RequestBody faqUpdateDto: PostUpdateRequestDto
    ): ResponseEntity<SuccessResponse<PostResponseDto>> {

        val updatedPost = postService.updatePost(clientId, postId, faqUpdateDto)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "글을 성공적으로 수정되었습니다.",
            data = PostResponseDto.from(updatedPost)
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Operation(summary = "게시글 삭제", description = "관리자 권한을 가진 사용자가 특정 게시글을 삭제합니다.")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{postId}")
    fun deletePost(
        @Parameter(description = "삭제할 게시글의 ID")
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