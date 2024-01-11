package kea.dpang.faq.entity

import jakarta.persistence.*


@Entity
data class Category(

    @Column(name = "category_name")
    val name: String, // 카테고리 이름 (자주 찾는 FAQ, 배송, 취소/교환/환불, 결제, 회원, 기타)

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
    val postLists: MutableList<Post> = mutableListOf() // FAQ 게시글 리스트

) {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    val id: Int? = null // 카테고리 식별자
}
