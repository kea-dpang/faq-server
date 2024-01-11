package kea.dpang.faq.exception

import java.lang.RuntimeException

class CategoryNotFoundException(id: Int): RuntimeException("존재하지 않는 카테고리입니다. (요청 카테고리 Id: $id)")
