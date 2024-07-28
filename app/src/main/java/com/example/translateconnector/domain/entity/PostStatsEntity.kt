package com.example.translateconnector.domain.entity

data class PostStatsEntity (
    val postId: String? = null,
    val viewer: Map<String, Boolean> = HashMap()
)
