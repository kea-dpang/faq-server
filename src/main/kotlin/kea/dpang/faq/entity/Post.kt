package kea.dpang.faq.entity

import jakarta.persistence.*
import kea.dpang.base.BaseEntity
import kea.dpang.faq.dto.PostResponse
import java.util.UUID

@Entity
@Table(name = "faq")
class Post(

    @Column(name = "question", nullable = false)
    var question: String, // 질문

    @Column(name = "answer", nullable = false)
    var answer: String, // 답변

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category?,

    @Column(name = "author_id", nullable = false)
    var authorId: UUID, // 작성자 식별자

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var postId: Int? = null // FAQ 게시글 식별자

) : BaseEntity() {

    fun toDto(): PostResponse {
        return PostResponse(
            postId = this.postId!!,
            question = this.question,
            answer = this.answer,
            categoryId = this.category!!.id!!,
            authorId = this.authorId,
            createdAt = this.createdAt!!
        )
    }

    // 연관 관계 편의 메소드
    fun modifyCategory(newCategory: Category) {
        this.category?.postLists?.remove(this)
        this.category = newCategory
        newCategory.postLists.add(this)
    }
}
