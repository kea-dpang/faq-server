package kea.dpang.faq.service

import kea.dpang.faq.dto.FAQCreateRequestDto
import kea.dpang.faq.dto.FAQUpdateRequestDto
import kea.dpang.faq.entity.Category
import kea.dpang.faq.entity.FAQ
import kea.dpang.faq.exception.FAQNotFoundException
import kea.dpang.faq.repository.FAQRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class FAQServiceImpl(
    private val faqRepository: FAQRepository
) : FAQService {

    override fun createFAQ(userId: UUID, faqCreateRequestDto: FAQCreateRequestDto): FAQ {
        // 새로운 게시글을 생성한다.
        val FAQ = FAQ(
            question = faqCreateRequestDto.question,
            answer = faqCreateRequestDto.answer,
            category = faqCreateRequestDto.category,
            authorId = userId
        )

        // Post를 저장하고 반환한다.
        return faqRepository.save(FAQ)
    }

    @Transactional(readOnly = true)
    override fun getFAQById(faqId: Long): FAQ {
        return faqRepository.findById(faqId).orElseThrow { FAQNotFoundException(faqId) }
    }

    @Transactional(readOnly = true)
    override fun getFAQsByCategory(category: Category): List<FAQ> {
        return faqRepository.findByCategory(category)
    }

    override fun updateFAQ(clientId: UUID, faqId: Long, faqUpdateRequestDto: FAQUpdateRequestDto): FAQ {
        // 게시글 조회. 없는 경우 예외 발생
        val post = faqRepository.findById(faqId)
            .orElseThrow { FAQNotFoundException(faqId) }

        // 해당 요청은 관리자 전용 요청으로, 기존 작성자와 같지 않더라도 게시글 수정 요청을 할 수 있음
        //  -> 사용자 비교 X

        // 게시글 내용 업데이트
        post.update(faqUpdateRequestDto)

        return faqRepository.save(post)
    }

    override fun deleteFAQ(faqId: Long) {
        // 게시글 조회. 없는 경우 예외 발생
        val post = faqRepository.findById(faqId)
            .orElseThrow { FAQNotFoundException(faqId) }

        // 해당 요청은 관리자 전용 요청으로, 기존 작성자와 같지 않더라도 게시글 삭제 요청을 할 수 있음
        //  -> 사용자 비교 X

        // 게시글 삭제
        faqRepository.delete(post)
    }

}