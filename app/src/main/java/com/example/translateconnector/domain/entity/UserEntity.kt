package com.example.translateconnector.domain.entity

data class UserEntity (
    var key: String? = null,
    var id: Int = 0,
    var avatar: String? = null,
    var status: String? = null,
    var name: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var countryCode: String? = null,
    var city: String? = null,
    var star: Float = 0f,
    var gender: Int = 0,
    var dob: String? = null,
    var phone: String? = null,
    var registerCountry: String? = null,
    var registerCity: String? = null,
    var isTranslator: Boolean = false,
    var friends: MutableMap<String, Boolean> = mutableMapOf(),
    var postStats: MutableMap<String, PostStatsEntity> = mutableMapOf(),
    val commentStats: MutableMap<String, CommentStatsEntity> = mutableMapOf()
//    var prioritySetting: String? = null
)