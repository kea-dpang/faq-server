package kea.dpang.faq.exception

class PostNotFoundException(postId: Long) : RuntimeException("해당 ID의 게시글을 찾을 수 없습니다. ID: $postId")
