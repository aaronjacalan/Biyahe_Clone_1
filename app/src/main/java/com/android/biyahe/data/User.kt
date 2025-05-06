package com.android.biyahe.data

data class User(
    val id: String = "",
    var username: String = "",
    var password: String = "",
    var bookmarkList: MutableList<String> = mutableListOf(),
    var shortDescription: String = "",
    var linkedAccounts: MutableList<Account> = mutableListOf()
)