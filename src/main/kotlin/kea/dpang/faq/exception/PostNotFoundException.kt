package kea.dpang.faq.exception

import java.lang.RuntimeException

class PostNotFoundException(postId: Int) : RuntimeException("해당 ID의 게시글을 찾을 수 없습니다. ID: $postId")
