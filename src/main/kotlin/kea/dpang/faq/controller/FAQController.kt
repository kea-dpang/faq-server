package kea.dpang.faq.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.faq.base.BaseResponse
import kea.dpang.faq.base.SuccessResponse
import kea.dpang.faq.dto.DeleteFAQsRequest
import kea.dpang.faq.dto.CreateFAQRequestDto
import kea.dpang.faq.dto.FAQResponseDto
import kea.dpang.faq.dto.UpdateFAQRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.service.FAQService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "FAQ")
@RequestMapping("/api/faq")
class FAQController(private val faqService: FAQService) {

    @Operation(summary = "FAQ 작성", description = "관리자 권한을 가진 사용자가 새로운 FAQ를 생성합니다.")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping
    fun createFAQ(
//        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "FAQ 생성 정보")
        @RequestBody faqCreateDto: CreateFAQRequestDto
    ): ResponseEntity<SuccessResponse<FAQResponseDto>> {

        val createdFAQ = faqService.createFAQ(clientId, faqCreateDto)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.CREATED.value(),
            message = "FAQ가 성공적으로 작성되었습니다.",
            data = FAQResponseDto.from(createdFAQ)
        )

        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }

    @Operation(summary = "FAQ 조회", description = "특정 FAQ를 조회합니다.")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping("/{faqId}")
    fun readFAQ(
        @Parameter(description = "조회할 FAQ의 ID")
        @PathVariable faqId: Long
    ): ResponseEntity<SuccessResponse<FAQResponseDto>> {

        val faq = faqService.getFAQById(faqId)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "FAQ를 성공적으로 조회하였습니다.",
            data = FAQResponseDto.from(faq)
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Deprecated(
        message = "카테고리별 FAQ 조회로 대체되었습니다.",
        replaceWith = ReplaceWith("readFAQsByCategory(pageable, null)")
    )
    @Operation(summary = "FAQ 전체 조회", description = "모든 FAQ를 페이지 단위로 조회합니다.")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping
    fun readAllFAQs(pageable: Pageable): ResponseEntity<SuccessResponse<Page<FAQResponseDto>>> {

        val faqsPage = faqService.getAllFAQs(pageable)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "FAQ를 성공적으로 조회하였습니다.",
            data = faqsPage.map { FAQResponseDto.from(it) }
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Operation(summary = "카테고리별 FAQ 조회", description = "특정 카테고리의 FAQ를 페이지네이션하여 조회합니다. 카테고리가 지정되지 않으면 모든 FAQ를 페이지네이션하여 조회합니다.")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN','SUPER_ADMIN')")
    @GetMapping("/category")
    fun readFAQsByCategory(
        @Parameter(description = "조회할 카테고리", required = false)
        @RequestParam(required = false) category: Category?,
        @Parameter(description = "페이지네이션 정보")
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<FAQResponseDto>>> {

        val faqsPage = category?.let {
            faqService.getFAQsByCategory(it, pageable)
        } ?: faqService.getAllFAQs(pageable)


        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "카테고리별 FAQ를 성공적으로 조회하였습니다.",
            data = faqsPage.map { FAQResponseDto.from(it) } // 페이지네이션된 결과를 DTO로 변환
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Operation(summary = "FAQ 수정", description = "관리자 권한을 가진 사용자가 특정 FAQ를 수정합니다.")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{faqId}")
    fun updateFAQ(
//        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "수정할 FAQ의 ID")
        @PathVariable faqId: Long,
        @Parameter(description = "FAQ 수정 정보")
        @RequestBody faqUpdateDto: UpdateFAQRequestDto
    ): ResponseEntity<SuccessResponse<FAQResponseDto>> {

        val updatedFAQ = faqService.updateFAQ(clientId, faqId, faqUpdateDto)

        // 응답 성공 객체 생성
        val successResponse = SuccessResponse(
            status = HttpStatus.OK.value(),
            message = "FAQ가 성공적으로 수정되었습니다.",
            data = FAQResponseDto.from(updatedFAQ)
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Deprecated(
        message = "여러 FAQ 삭제로 대체되었습니다.",
        replaceWith = ReplaceWith("deleteFAQs")
    )
    @Operation(summary = "FAQ 삭제", description = "관리자 권한을 가진 사용자가 특정 FAQ를 삭제합니다.")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{faqId}")
    fun deleteFAQ(
        @Parameter(description = "삭제할 FAQ의 ID")
        @PathVariable faqId: Long
    ): ResponseEntity<BaseResponse> {

        faqService.deleteFAQ(faqId)

        // 응답 성공 객체 생성
        val successResponse = BaseResponse(
            status = HttpStatus.OK.value(),
            message = "FAQ를 성공적으로 삭제하였습니다."
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @Operation(summary = "여러 FAQ 삭제", description = "관리자 권한을 가진 사용자가 여러 FAQ를 한 번에 삭제합니다.")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/list")
    fun deleteFAQs(
        @Parameter(description = "삭제할 FAQ들의 ID 리스트")
        @RequestBody request: DeleteFAQsRequest
    ): ResponseEntity<BaseResponse> {

        faqService.deleteFAQs(request.faqIds)

        // 응답 성공 객체 생성
        val successResponse = BaseResponse(
            status = HttpStatus.OK.value(),
            message = "FAQ들을 성공적으로 삭제하였습니다."
        )

        return ResponseEntity(successResponse, HttpStatus.OK)
    }

}