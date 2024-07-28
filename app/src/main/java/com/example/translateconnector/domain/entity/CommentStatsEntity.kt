package com.example.translateconnector.domain.entity

data class CommentStatsEntity(
    val postId: String? = null,
    val commentIds: Map<String, Boolean> = HashMap()
)
