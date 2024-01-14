package kea.dpang.faq.exception

class CategoryNotFoundException(name: String): RuntimeException("존재하지 않는 카테고리입니다. (요청 카테고리: $name)")
