package kea.dpang.faq.entity

import jakarta.persistence.*
import kea.dpang.base.BaseEntity
import java.util.UUID

@Entity
@Table(name = "FAQ")
data class Post(

    @Column(name = "question", nullable = false)
    var question: String, // 질문

    @Column(name = "answer", nullable = false)
    var answer: String, // 답변

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    val category: Category,

    @Column(name = "author_id", nullable = false)
    var authorId: UUID // 작성자 식별자

) : BaseEntity() {

    @Id
    @Column(name = "faq_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null // FAQ 질문 식별자
}
