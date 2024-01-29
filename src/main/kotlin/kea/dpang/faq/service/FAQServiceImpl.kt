package kea.dpang.faq.service

import kea.dpang.faq.dto.FAQCreateRequestDto
import kea.dpang.faq.dto.FAQUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import kea.dpang.faq.exception.FAQNotFoundException
import kea.dpang.faq.repository.FAQRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FAQServiceImpl(
    private val faqRepository: FAQRepository
) : FAQService {

    private val logger = LoggerFactory.getLogger(FAQServiceImpl::class.java)

    override fun createFAQ(userId: Long, faqCreateRequestDto: FAQCreateRequestDto): FAQ {
        logger.info("[FAQ 생성] 사용자 ID: $userId, FAQ 정보: $faqCreateRequestDto")

        // 새로운 게시글을 생성한다.
        val faq = FAQ(
            question = faqCreateRequestDto.question,
            answer = faqCreateRequestDto.answer,
            category = faqCreateRequestDto.category,
            authorId = userId
        )

        // Post를 저장하고 반환한다.
        return faqRepository.save(faq).also {
            logger.info("[FAQ 생성 완료] 생성된 FAQ ID: ${it.id}")
        }
    }

    @Transactional(readOnly = true)
    override fun getAllFAQs(pageable: Pageable): Page<FAQ> {
        logger.info("[FAQ 전체 조회]")

        return faqRepository.findAll(pageable).also {
            logger.info("[FAQ 전체 조회 완료] 조회된 FAQ 페이지: ${it.number + 1}, 페이지당 FAQ 개수: ${it.size}, 전체 FAQ 개수: ${it.totalElements}")
        }
    }

    @Transactional(readOnly = true)
    override fun getFAQById(faqId: Long): FAQ {
        logger.info("[FAQ 조회] 조회 요청 FAQ ID: $faqId")

        return faqRepository.findById(faqId).orElseThrow {
            FAQNotFoundException(faqId)
        }.also {
            logger.info("[FAQ 조회 완료] 조회된 FAQ 정보: $it")
        }
    }

    @Transactional(readOnly = true)
    override fun getFAQsByCategory(category: Category): List<FAQ> {
        logger.info("[카테고리별 FAQ 조회] 조회 요청 카테고리: $category")

        return faqRepository.findByCategory(category).also {
            logger.info("[카테고리별 FAQ 조회 완료] 조회된 FAQ 개수: ${it.size}")
        }
    }

    override fun updateFAQ(clientId: Long, faqId: Long, faqUpdateRequestDto: FAQUpdateRequestDto): FAQ {
        logger.info("[FAQ 수정] 클라이언트 ID: $clientId, 수정 요청 FAQ ID: $faqId, 수정 정보: $faqUpdateRequestDto")

        // 게시글 조회. 없는 경우 예외 발생
        val post = faqRepository.findById(faqId)
            .orElseThrow { FAQNotFoundException(faqId) }

        // 해당 요청은 관리자 전용 요청으로, 기존 작성자와 같지 않더라도 게시글 수정 요청을 할 수 있음
        //  -> 사용자 비교 X

        // 게시글 내용 업데이트
        post.update(faqUpdateRequestDto)

        return faqRepository.save(post).also {
            logger.info("[FAQ 수정 완료] 수정된 FAQ 정보: $it")
        }
    }

    override fun deleteFAQ(faqId: Long) {
        logger.info("[FAQ 삭제] 삭제 요청 FAQ ID: $faqId")

        // 게시글 조회. 없는 경우 예외 발생
        val post = faqRepository.findById(faqId)
            .orElseThrow { FAQNotFoundException(faqId) }

        // 해당 요청은 관리자 전용 요청으로, 기존 작성자와 같지 않더라도 게시글 삭제 요청을 할 수 있음
        //  -> 사용자 비교 X

        // 게시글 삭제
        faqRepository.delete(post).also {
            logger.info("[FAQ 삭제 완료] 삭제된 FAQ ID: $faqId")
        }
    }

}