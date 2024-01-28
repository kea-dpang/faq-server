package kea.dpang.faq.entity

import com.fasterxml.jackson.annotation.JsonCreator

enum class Category {
    FAQ, // 자주 찾는 FAQ
    SHIPPING, // 배송 관련
    CANCELLATION_REFUND_EXCHANGE, // 취소/교환/환불 관련
    PAYMENT, // 결제 관련
    MEMBER, // 회원 관련
    ETC; // 기타

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): Category {
            return Category.valueOf(value.uppercase())
        }
    }
}