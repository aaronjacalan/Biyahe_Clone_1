package com.android.biyahe.data

data class Account(
    val id: String = "",
    val displayName: String = "",
    val link: String = "",
    val iconType: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "displayName" to displayName,
        "link" to link,
        "iconType" to iconType
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Account = Account(
            id = map["id"] as? String ?: "",
            displayName = map["displayName"] as? String ?: "",
            link = map["link"] as? String ?: "",
            iconType = map["iconType"] as? String ?: ""
        )
    }
}