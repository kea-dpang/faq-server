package kea.dpang.faq.service

import kea.dpang.faq.dto.CreateFAQRequestDto
import kea.dpang.faq.dto.UpdateFAQRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * FAQ 관련 서비스를 제공하는 인터페이스
 */
interface FAQService {

    /**
     * 새로운 FAQ를 생성합니다.
     *
     * @param userId 사용자 ID
     * @param createFAQRequestDto FAQ 생성 요청 DTO
     * @return 생성된 FAQ
     */
    fun createFAQ(userId: Long, createFAQRequestDto: CreateFAQRequestDto): FAQ

    /**
     * 모든 FAQ를 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @return 조회된 FAQ 페이지
     */
    fun getAllFAQs(pageable: Pageable): Page<FAQ>

    /**
     * 특정 ID를 가진 FAQ를 조회합니다.
     *
     * @param faqId FAQ ID
     * @return 조회된 FAQ
     */
    fun getFAQById(faqId: Long): FAQ

    /**
     * 특정 카테고리에 속하는 FAQ를 페이지네이션하여 조회합니다.
     *
     * @param category 카테고리
     * @param pageable 페이지네이션 정보
     * @return 조회된 FAQ 페이지
     */
    fun getFAQsByCategory(category: Category, pageable: Pageable): Page<FAQ>

    /**
     * 특정 ID를 가진 FAQ를 업데이트합니다.
     *
     * @param clientId 클라이언트 ID
     * @param faqId FAQ ID
     * @param updateFAQRequestDto FAQ 업데이트 요청 DTO
     * @return 업데이트된 FAQ
     */
    fun updateFAQ(clientId: Long, faqId: Long, updateFAQRequestDto: UpdateFAQRequestDto): FAQ

    /**
     * 특정 ID를 가진 FAQ를 삭제합니다.
     *
     * @param faqId FAQ ID
     */
    fun deleteFAQ(faqId: Long)

    /**
     * 주어진 ID 리스트에 해당하는 FAQ들을 삭제합니다.
     *
     * @param faqIds FAQ ID 리스트
     */
    fun deleteFAQs(faqIds: List<Long>)

}