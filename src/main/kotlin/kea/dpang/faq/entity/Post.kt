package kea.dpang.faq.entity

import jakarta.persistence.*
import kea.dpang.faq.base.BaseEntity
import java.util.*

@Entity
@Table(name = "faq")
class Post(

    @Column(name = "question", nullable = false)
    var question: String, // 질문

    @Column(name = "answer", nullable = false)
    var answer: String, // 답변

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: Category, // 카테고리

    @Column(name = "author_id", nullable = false)
    var authorId: UUID, // 작성자 식별자

    @Id
    @Column(name = "faq_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null // FAQ 게시글 식별자

) : BaseEntity()
